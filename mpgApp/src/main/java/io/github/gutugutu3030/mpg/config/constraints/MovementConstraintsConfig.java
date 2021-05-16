package io.github.gutugutu3030.mpg.config.constraints;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.util.Vector;

/** 傾きと平行移動の制約条件 [平行移動のMAX（mm）] = [傾き（rad）] x a + b */
public class MovementConstraintsConfig extends AbstractConfig {
  public double a = -90;
  public double b = 40;

  /**
   * 制約条件に移動量を傾きがあっているか調べます
   *
   * @param position 移動量
   * @param angle 回転量
   * @return 制約をみたしているか
   */
  public boolean met(Vector position, double angle) {
    return position.mag() <= angle * a + b;
  }
}
