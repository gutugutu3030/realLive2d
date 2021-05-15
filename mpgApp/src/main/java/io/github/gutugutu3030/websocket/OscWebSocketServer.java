package io.github.gutugutu3030.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.osc.OscMethod;
import io.github.gutugutu3030.osc.OscMethodType;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** OSCのようにWebSocketを送受信するWebSocketサーバ */
public class OscWebSocketServer extends WebSocketServer {

  private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

  /** 受信メソッドの書かれたクラス */
  private Object methodClass;

  public OscWebSocketServer(Object methodClass, WebsocketConfig config) {
    super(new InetSocketAddress(config.port));
    this.methodClass = methodClass;
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    log.info("connect {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    log.info("disconnect {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Optional.ofNullable(mapper.readValue(message, WebSocketJson.class))
          .ifPresent(this::onMessageExec);
    } catch (JsonProcessingException e) {
      // OSCメッセージでなさそうなのでバンドルとして解析する
      log.debug(message);
      try {
        Optional.ofNullable(mapper.readValue(message, new TypeReference<List<WebSocketJson>>() {}))
            .ifPresent(l -> l.forEach(this::onMessageExec));
      } catch (JsonProcessingException e1) {
        log.warn("json read error", e1);
      }
    }
  }

  /**
   * フロントからのJsonオブジェクトをメソッドに割り当てて実行します
   *
   * @param json 受け取ったJSON
   */
  private void onMessageExec(WebSocketJson json) {
    log.debug("json: {}", json);
    List<Object> arguments = json.getParsedArgs();
    Arrays.stream(methodClass.getClass().getDeclaredMethods()) //
        .filter(
            m ->
                Arrays.stream(m.getDeclaredAnnotations()) //
                    .filter(OscMethod.class::isInstance)
                    .map(OscMethod.class::cast)
                    .findAny() //
                    .filter(
                        a ->
                            Arrays.asList(a.using()).contains(OscMethodType.WEBSOCKET)) // 対応タイプかどうか
                    .map(OscMethod::addr)
                    .map(json.address::equals)
                    .orElse(false))
        .findAny()
        .ifPresent(
            m -> {
              List<Class<?>> parameterType =
                  Arrays.stream(m.getParameterTypes()).collect(Collectors.toList());
              try {
                if (parameterType.size() == 0) {
                  m.invoke(methodClass);
                  return;
                }
                if (parameterType.size() == 1
                    && List.class.isAssignableFrom(parameterType.get(0))) {
                  m.invoke(methodClass, arguments);
                  return;
                }
                m.invoke(methodClass, arguments.toArray());
              } catch (Exception e) {
                log.error("failed invoke WebsocketMethod", e);
              }
            });
  }

  @Override
  public void onError(WebSocket conn, Exception e) {
    log.error("websocket server error.", e);
  }

  @Override
  public void onStart() {
    log.info("start.");
    setConnectionLostTimeout(500);
  }

  /**
   * メッセージをフロントに送信します
   *
   * @param message メッセージ
   */
  public void sendOscMessage(OSCMessage message) {
    Map<String, Object> map = new HashMap<>();
    map.put("address", message.getAddress());
    map.put("args", message.getArguments());
    try {
      this.broadcast(new ObjectMapper().writeValueAsString(map));
    } catch (JsonProcessingException e) {
      log.error("message json send failed", e);
    }
  }

  /**
   * OSCバンドルをフロントに送信します
   *
   * @param messages メッセージ
   */
  public void sendOscBundle(OSCMessage... messages) {
    try {
      this.broadcast(
          new ObjectMapper()
              .writeValueAsString(
                  Arrays.stream(messages)
                      .map(
                          message -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("address", message.getAddress());
                            map.put("args", message.getArguments());
                            return map;
                          })
                      .collect(Collectors.toList())));
    } catch (JsonProcessingException e) {
      log.error("bundle json send failed", e);
    }
  }

  /**
   * OSCバンドルをフロントに送信します
   *
   * @param messages メッセージ
   */
  public void sendOscBundle(List<? extends OSCMessage> messages) {
    try {
      this.broadcast(
          new ObjectMapper()
              .writeValueAsString(
                  messages.stream()
                      .map(
                          message -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("address", message.getAddress());
                            map.put("args", message.getArguments());
                            return map;
                          })
                      .collect(Collectors.toList())));
    } catch (JsonProcessingException e) {
      log.error("bundle json send failed", e);
    }
  }

  /**
   * OSCバンドルをフロントに送信します<br>
   * OSCMessageでないパケットが含まれている場合、そのデータは送信しません。
   *
   * @param bundle バンドル
   */
  public void sendOscBundle(OSCBundle bundle) {
    try {
      this.broadcast(
          new ObjectMapper()
              .writeValueAsString(
                  bundle.getPackets().stream()
                      .filter(OSCMessage.class::isInstance)
                      .map(OSCMessage.class::cast)
                      .map(
                          message -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("address", message.getAddress());
                            map.put("args", message.getArguments());
                            return map;
                          })
                      .collect(Collectors.toList())));
    } catch (JsonProcessingException e) {
      log.error("bundle json send failed", e);
    }
  }

  /**
   * そのパラメータタイプが一致しているか調べます
   *
   * @param methodTypes メソッドのクラス配列
   * @param jsonType jsonに記述されたパラメータタイプ
   * @return 一致しているかどうか
   */
  private boolean canChangeArgType(List<Class<?>> methodTypes, List<Object> args) {
    if (methodTypes.size() != args.size()) {
      return false;
    }
    return IntStream.range(0, methodTypes.size())
        .allMatch(i -> methodTypes.get(i).isInstance(args.get(i)));
  }
}
