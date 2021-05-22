package io.github.gutugutu3030.mpg.config;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.mpg.config.constraints.MovementConstraintsConfig;
import io.github.gutugutu3030.mpg.config.interpolation.InterpolationConfig;
import io.github.gutugutu3030.mpg.config.panel.PanelConfig;
import io.github.gutugutu3030.mpg.config.servo.ServoConfig;
import io.github.gutugutu3030.osc.OscConfig;
import io.github.gutugutu3030.websocket.WebsocketConfig;

/** コンフィグ */
public class Config extends AbstractConfig {
  public WebsocketConfig websocket;
  public OscConfig osc;
  public ServoConfig servo;
  public PanelConfig panel;
  public MovementConstraintsConfig movementConstraints;
  public InterpolationConfig interpolation;
}
