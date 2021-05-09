package io.github.gutugutu3030.mpg.layer;

import io.github.gutugutu3030.mpg.config.Config;
import io.github.gutugutu3030.mpg.config.constraints.MovementConstraintsConfig;
import io.github.gutugutu3030.mpg.layer.servo.Servo;
import io.github.gutugutu3030.util.Pair;
import io.github.gutugutu3030.util.Vector;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * パネル1枚<br>
 * 移動してないときのパネル中央を座標0とします。<br>
 */
public class Layer {
  /** レイヤーを動かすサーボ */
  private Servo servoX, servoY1, servoY2;

  /** パネルのサイズ(mm) */
  private Vector size;

  /** y軸を制御するサーボの回転軸間隔(mm) */
  private double distanceOfServoY;

  /** パネルに掘られた溝の、端からの距離(mm) */
  private double railPosition;

  /** サーボにつけられたアームの長さ(mm) */
  private double armLength;

  /** 0で30度の位置が真ん中 数値を増やすとその分だけサーボがパネルから離れます */
  private double offsetOfServo;

  /** x軸を制御するサーボの回転軸Y座標(mm) */
  private double servoXsY;

  /** 傾きと平行移動の制約条件 */
  private MovementConstraintsConfig movementConstraints;

  /** パネルの回転角度(度) */
  private double angle;

  /** パネル中心の座標(mm) */
  private Vector position;

  public Layer(Config config) {
    size = new Vector(config.panel.w, config.panel.h);
    railPosition = config.panel.railPosition;
    armLength = config.servo.armLength;
    offsetOfServo = config.servo.positionOffset;
    distanceOfServoY = config.servo.distanceOfServoY;
    servoXsY = config.servo.yOfServoX;
    movementConstraints = config.movementConstraints;

    angle = 0;
    position = new Vector();

    servoY1 = new Servo();
    servoY2 = new Servo();
    servoX = new Servo();
    setServoPosition();
  }

  /**
   * パネルの移動量と回転量を設定します
   *
   * @param position 移動量
   * @param angle 回転量
   */
  public boolean set(Vector position, double angle) {
    if (!movementConstraints.met(position, angle)) {
      return false;
    }
    if (Math.abs(angle) < 0.0001) {
      // 回転なし
      servoY1.setAngle(Math.asin((size.y + offsetOfServo) / armLength + 0.5));
      servoY2.setAngle(Math.PI - Math.asin((size.y + offsetOfServo) / armLength + 0.5));
      servoX.setAngle(Math.asin((size.x + offsetOfServo) / armLength + 0.5));
      return true;
    }
    double armL = armLength;
    Stream<Pair<Servo, Optional<Double>>> streamY, streamX;
    {
      // y軸モーター
      double mizoDY = size.y / 2 - railPosition; // 中心から下ミゾまでの長さ
      // 溝の直線の式
      final double m = Math.tan(Math.toRadians(angle));
      final double n = -mizoDY / Math.cos(Math.toRadians(angle)) - m * position.x + position.y;
      streamY =
          Stream.of(servoY1, servoY2).map(s -> new Pair<>(s, s.getNewAngleFromLine(armL, m, n)));
    }
    {
      // x軸モーター
      double mizoDX = size.x / 2 - railPosition; // 中心から横ミゾまでの長さ
      // 溝の直線の式
      final double m = Math.tan(Math.toRadians(angle) + Math.PI / 2);
      final double n = -mizoDX / Math.sin(Math.toRadians(angle)) - m * position.x + position.y;
      streamX = Stream.of(servoX).map(s -> new Pair<>(s, s.getNewAngleFromLine(armL, m, n)));
    }
    List<Pair<Servo, Optional<Double>>> candidates =
        Stream.of(streamX, streamY).flatMap(s -> s).collect(Collectors.toList());
    if (!candidates.stream().allMatch(p -> p.getValue().isPresent())) {
      return false;
    }
    this.angle = angle;
    this.position = position;
    candidates.forEach(p -> p.getValue().ifPresent(a -> p.getKey().setAngle(a)));
    return true;
  }

  /**
   * 現在の角度でのPWMの数値を取得します
   *
   * @return PWMのリスト
   */
  public List<Integer> getPWMList() {
    return Stream.of(servoX, servoY1, servoY2).map(Servo::getPWM).collect(Collectors.toList());
  }

  /**
   * 現在の座標と角度を取得します
   *
   * @return 座標と角度のペア
   */
  public Pair<Vector, Double> get() {
    return new Pair<>(this.position, this.angle);
  }

  /** サーボの回転軸座標をセットします */
  private void setServoPosition() {
    double servoYRail = -size.y / 2 + railPosition; // 高さレールの座標
    double servoY = servoYRail - armLength / 2 - offsetOfServo; // 高さサーボの回転軸高さ
    double servoXRail = servoYRail; // 横レールの座標

    servoY1.setPosition(new Vector(-distanceOfServoY / 2, servoY));
    servoY2.setPosition(new Vector(-distanceOfServoY / 2, servoY));
    servoX.setPosition(new Vector(servoXRail - armLength / 2 - offsetOfServo, servoXsY));
  }
}
