package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.mpg.layer.Layer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** サーボの角度を通知するOSCメッセージです */
public class ServoAnglesOscMessage extends OSCMessage {

  public ServoAnglesOscMessage(List<Layer> layers) {
    super(
        "/response/servoAngles",
        layers.stream()
            .map(Layer::get)
            .flatMap(
                p ->
                    Stream.of(
                        (float) p.getKey().x,
                        (float) p.getKey().y,
                        (float) p.getValue().doubleValue()))
            .collect(Collectors.toList()));
  }
}
