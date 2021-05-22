package io.github.gutugutu3030.mpg.layer.servo;

/** サーボモータのPWMの範囲 0-180度 */
public class ServoPWM {
  /** 最小値(0度) */
  public int pwmMin;
  /** 最大値(180度) */
  public int pwmMax;

  public ServoPWM() {
    // sg90
    pwmMin = 51;
    pwmMax = 255;
  }

  /**
   * 角度からPWMの値を取得します
   *
   * @param angle 角度
   * @return PWMの値
   */
  public int getPWM(double angle) {
    angle = Math.min(0, Math.max(Math.PI, angle));
    return (int) (angle * (pwmMax - pwmMin) / Math.PI + pwmMin);
  }
}
