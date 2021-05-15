package io.github.gutugutu3030.mpg.config;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.mpg.config.constraints.MovementConstraintsConfig;
import io.github.gutugutu3030.mpg.config.panel.PanelConfig;
import io.github.gutugutu3030.mpg.config.servo.ServoConfig;
import io.github.gutugutu3030.websocket.WebsocketConfig;

/** コンフィグ */
public class Config extends AbstractConfig {
  public WebsocketConfig websocket;
  public ServoConfig servo;
  public PanelConfig panel;
  public MovementConstraintsConfig movementConstraints;
}