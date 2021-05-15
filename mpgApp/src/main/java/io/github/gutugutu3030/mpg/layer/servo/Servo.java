package io.github.gutugutu3030.mpg.layer.servo;

import io.github.gutugutu3030.util.Vector;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** サーボモータの情報を管理します。 */
public class Servo {

  /**
   * 円と直線の交点を求めます
   *
   * @param cr 円の半径
   * @param cx 円の中心X座標
   * @param cy 円の中心Y座標
   * @param m 直線の式
   * @param n 直線の式
   * @return 解
   */
  private static List<Vector> getIntersectionPoint(
      double cr, double cx, double cy, double m, double n) {
    double a = 1 + Math.pow(m, 2);
    double b = -2 * cx + 2 * m * (n - cy);
    double c = Math.pow(cx, 2) + Math.pow((n - cy), 2) - Math.pow(cr, 2);
    double D = Math.pow(b, 2) - 4 * a * c;
    if (D < 0) {
      return List.of();
    }
    double x1 = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
    if (D < 0.00001) {
      return List.of(new Vector(x1, m * x1 + n));
    }
    double x2 = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
    return List.of(new Vector(x1, m * x1 + n), new Vector(x2, m * x2 + n));
  }

  /** 回転軸の座標 */
  protected Vector position;

  /** 0度のときの角度 */
  protected double defaultAngle;

  /** 現在の角度(radians) */
  protected double angle;

  /** PWMの設定値 */
  protected ServoPWM pwm;

  public Servo() {
    this.position = new Vector();
    pwm = new ServoPWM();
  }

  public void setPosition(Vector position) {
    this.position = position;
  }

  public double getAngle() {
    return this.angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  /**
   * 現在の角度でのPWM値を取得します
   *
   * @return
   */
  public int getPWM() {
    return Math.min(
        pwm.pwmMax,
        Math.max(
            pwm.pwmMin, (int) (angle / (Math.PI / 2) * (pwm.pwmMax - pwm.pwmMin) + pwm.pwmMin)));
  }

  /**
   * y=mx+nの直線上に乗るような回転角を取得します。<br>
   * そのような角度がない場合、Optional.empty()を返します
   *
   * @param armLength アームの長さ
   * @param m 直線の式
   * @param n 直線の式
   * @return 角度
   */
  public Optional<Double> getNewAngleFromLine(double armLength, double m, double n) {
    List<Vector> results = getIntersectionPoint(armLength, position.x, position.y, m, n);
    return results.stream()
        .sorted(Comparator.comparing(Vector::mag)) // 原点に近い交点を使用する
        .findFirst() // その一番近い頂点だけ取得
        .filter(p -> p.dist(position) <= armLength) // 交点とサーボ回転軸がアームの長さより離れてないかチェック
        .map(p -> Math.atan2(p.y - position.y, p.x - position.x));
  }

  public String toString() {
    return String.format("Servo[angle=%f]", this.angle);
  }
}
