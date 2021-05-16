package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.mpg.layer.Layer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** サーボの角度を通知するOSCメッセージです */
public class SetLayerPositionOscMessage extends OSCMessage {

  public SetLayerPositionOscMessage(List<Layer> layers) {
    super(
        "/setLayerPosition",
        layers.stream()
            .map(Layer::get)
            .flatMap(
                p ->
                    Stream.concat(
                        Stream.of(
                            (float) p.getKey().x,
                            (float) p.getKey().y,
                            (float) p.getValue().getKey().doubleValue()),
                        p.getValue().getValue().stream()))
            .collect(Collectors.toList()));
  }
}
