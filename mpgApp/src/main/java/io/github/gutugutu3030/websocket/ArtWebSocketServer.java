package io.github.gutugutu3030.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.mpg.config.Config;
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

public class ArtWebSocketServer extends WebSocketServer {

  private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

  /** 受信メソッドの書かれたクラス */
  private Object methodClass;

  public ArtWebSocketServer(Object methodClass, Config config) {
    super(
        new InetSocketAddress(
            Optional.ofNullable(config).map(c -> c.websocket).map(c -> c.port).orElse(8080)));
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
    log.debug("json:{} - {}", json.address, json.args);
    Arrays.stream(methodClass.getClass().getDeclaredMethods()) //
        .filter(
            m ->
                Arrays.stream(m.getDeclaredAnnotations()) //
                    .filter(WebSocketMethod.class::isInstance)
                    .map(WebSocketMethod.class::cast)
                    .findAny() //
                    .map(WebSocketMethod::addr)
                    .map(json.address::equals)
                    .orElse(false))
        .findAny()
        .ifPresent(
            m -> {
              Class<?> parameterType[] = m.getParameterTypes();
              try {
                if (parameterType.length == 0) {
                  m.invoke(methodClass);
                  return;
                }
                if (parameterType.length == 1 && List.class.isAssignableFrom(parameterType[0])) {
                  m.invoke(methodClass, json.args);
                  return;
                }
                if (parameterType.length == json.args.size()
                    && //
                    IntStream.range(0, parameterType.length)
                        .allMatch(
                            i -> //
                            parameterType[i].isInstance(json.args.get(i)))) {
                  m.invoke(methodClass, json.args.toArray());
                  return;
                }
                throw new IllegalArgumentException("unknown args");
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
}
