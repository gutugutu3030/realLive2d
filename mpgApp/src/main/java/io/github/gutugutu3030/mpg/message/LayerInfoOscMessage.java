package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import io.github.gutugutu3030.mpg.config.constraints.MovementConstraintsConfig;
import io.github.gutugutu3030.util.Vector;
import java.util.List;

public class LayerInfoOscMessage extends OSCMessage {
  public LayerInfoOscMessage(
      Vector size,
      float distanceOfServoY,
      float railPosition,
      float armLength,
      float offsetOfServo,
      float servoXsY,
      MovementConstraintsConfig movementConstraints) {
    super(
        "/response/layerInfo",
        List.of(
            (float) size.x,
            (float) size.y,
            railPosition,
            armLength,
            offsetOfServo,
            servoXsY,
            (float) movementConstraints.a,
            (float) movementConstraints.b));
  }
}
