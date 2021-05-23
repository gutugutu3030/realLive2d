package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.mpg.layer.Layer;
import io.github.gutugutu3030.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

/** サーボの角度を通知するOSCメッセージです */
public class SetServoDefaultAnglesOscMessage extends OSCMessage {

    public SetServoDefaultAnglesOscMessage(List<Layer> layers) {
        super("/setServoDefaultAngles", layers.stream()
                .flatMap(l -> l.getServoDefaultAngles().stream().map(Pair::getValue)).collect(Collectors.toList()));
    }
}
