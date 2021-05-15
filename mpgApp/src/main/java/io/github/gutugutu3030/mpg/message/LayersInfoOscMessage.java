package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LayersInfoOscMessage extends OSCMessage {
  public LayersInfoOscMessage(Stream<LayerInfoOscMessage> stream) {
    super(
        "/setLayersInfo",
        stream.flatMap(m -> m.getArguments().stream()).collect(Collectors.toList()));
  }
}
