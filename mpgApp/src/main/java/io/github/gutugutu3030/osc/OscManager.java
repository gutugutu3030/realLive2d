package io.github.gutugutu3030.osc;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.transport.udp.OSCPortIn;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Oscの送受信を管理します */
public class OscManager implements OSCPacketListener {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  /** 受信ポート */
  OSCPortIn portIn;

  /** 受信メソッドの書かれたクラス */
  private Object methodClass;

  public OscManager(Object methodClass, OscConfig config) {
    this.methodClass = methodClass;
    try {
      portIn = new OSCPortIn(config.port);
      portIn.addPacketListener(this);
    } catch (Exception e) {
      log.error("failed init oscPortIn", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void handlePacket(OSCPacketEvent event) {
    Optional.of(event.getPacket())
        .map(
            p -> {
              if (p instanceof OSCBundle) {
                return ((OSCBundle) p)
                    .getPackets().stream()
                        .filter(OSCMessage.class::isInstance)
                        .map(OSCMessage.class::cast)
                        .collect(Collectors.toList());
              }
              if (p instanceof OSCMessage) {
                return List.of((OSCMessage) p);
              }
              log.warn("not OSCMessage.");
              return null;
            })
        .ifPresentOrElse(
            l -> l.forEach(this::execOSCMessage), () -> log.error("not contain OSCMessage."));
  }

  /**
   * OSCメッセージから受信メソッドを実行します
   *
   * @param mes 受信メッセージ
   */
  protected void execOSCMessage(OSCMessage mes) {
    log.debug("exec oscMessage: {}-{}", mes.getAddress(), mes.getArguments());
    OscExecuter.exec(mes.getAddress(), mes.getArguments(), methodClass, OscMethodType.OSC);
  }

  /** {@inheritDoc} */
  public void handleBadData(OSCBadDataEvent event) {}
}
