package io.github.gutugutu3030.mpg.config.constraints;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.util.Pair;
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
    return position.mag() <= getMaxMagnitude(angle);
  }

  /**
   * 角度から平行移動可能な移動最大量を求めます
   *
   * @param angle 角度(rad)
   * @return 平行移動可能な移動最大量
   */
  public double getMaxMagnitude(double angle) {
    return Math.abs(angle) * a + b;
  }

  /**
   * 傾けられる角度を考慮して回転量を返します
   *
   * @param angle 傾けたい量
   * @return 傾けられる量
   */
  public double checkAngle(double angle) {
    double maxRotateAngle = getMaxRotateAngle();
    if (angle < -maxRotateAngle) {
      return -maxRotateAngle;
    }
    if (angle > maxRotateAngle) {
      return maxRotateAngle;
    }
    return angle;
  }

  /**
   * -1から1でスケールされた各種値を実際の数値に変換します.
   *
   * @param x[-1~1]
   * @param y[-1~1]
   * @param angle[-1~1]
   * @return xy移動量(mm)と角度(rad)のペア
   */
  public Pair<Vector, Double> mapScaledPosition(double x, double y, double angle) {
    double rad = getMaxRotateAngle() * angle;
    double mag = Math.max(Math.abs(x), Math.abs(y)) * getMaxMagnitude(rad);
    double theta = Math.atan2(y, x);
    return new Pair<>(new Vector(mag * Math.cos(theta), mag * Math.sin(theta)), rad);
  }

  private double getMaxRotateAngle() {
    if (maxRotateAngle == null) {
      maxRotateAngle = Math.toRadians(this.maxRotateAngleDegree);
    }
    return maxRotateAngle;
  }
}
