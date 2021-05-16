package io.github.gutugutu3030.mpg.config.constraints;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.util.Vector;

/** 傾きと平行移動の制約条件 [平行移動のMAX（mm）] = [傾き（rad）] x a + b */
public class MovementConstraintsConfig extends AbstractConfig {
  /** [平行移動のMAX（mm）] = [傾き（rad）] x a + b */
  public double a = -90;
  /** [平行移動のMAX（mm）] = [傾き（rad）] x a + b */
  public double b = 40;

  /** 最高で傾けられるアングル(度) */
  public double maxRotateAngleDegree = 15;
  /** 最高で傾けられるアングル(rad) 自動代入 */
  private Double maxRotateAngle = null;

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

  /**
   * 傾けられる角度を考慮して回転量を返します
   *
   * @param angle 傾けたい量
   * @return 傾けられる量
   */
  public double checkAngle(double angle) {
    if (maxRotateAngle == null) {
      maxRotateAngle = Math.toRadians(this.maxRotateAngleDegree);
    }
    if (angle < -maxRotateAngle) {
      return -maxRotateAngle;
    }
    if (angle > maxRotateAngle) {
      return maxRotateAngle;
    }
    return angle;
  }
}
