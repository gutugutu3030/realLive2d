package io.github.gutugutu3030.mpg.layer;

import io.github.gutugutu3030.mpg.config.Config;
import io.github.gutugutu3030.mpg.config.constraints.MovementConstraintsConfig;
import io.github.gutugutu3030.mpg.layer.servo.Servo;
import io.github.gutugutu3030.mpg.layer.servo.ServoOffset;
import io.github.gutugutu3030.mpg.message.LayerInfoOscMessage;
import io.github.gutugutu3030.util.Pair;
import io.github.gutugutu3030.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * パネル1枚<br>
 * 移動してないときのパネル中央を座標0とします。<br>
 */
public class Layer {
  private Logger log = LoggerFactory.getLogger(this.getClass());
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

  /** 反対向きかどうか */
  private boolean isReverse;

  public Layer(Config config, boolean isReverse) {
    size = new Vector(config.panel.w, config.panel.h);
    railPosition = config.panel.railPosition;
    armLength = config.servo.armLength;
    offsetOfServo = config.servo.positionOffset;
    distanceOfServoY = config.servo.distanceOfServoY;
    servoXsY = config.servo.yOfServoX;
    movementConstraints = config.movementConstraints;

    this.isReverse = isReverse;

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
   * @param angle    回転量(rad)
   */
  public boolean set(Vector position, double angle) {
    if (isReverse) {
      log.trace("set reverse");
      position.x *= -1;
      position.y *= -1;
    }
    log.trace("set layer - ({},{}) {}rad", position.x, position.y, angle);
    angle = movementConstraints.checkAngle(angle);
    if (!movementConstraints.met(position, angle)) {
      log.warn("out of movement constraints");
      return false;
    }
    this.position = position;
    this.angle = angle;
    if (Math.abs(angle) < 0.0001) {
      // 回転なし
      log.trace("no rotate calc");
      servoY1.setAngle(Math.asin((position.y + offsetOfServo) / armLength + 0.5));
      servoY2.setAngle(Math.PI - Math.asin((position.y + offsetOfServo) / armLength + 0.5));
      servoX.setAngle(Math.asin((position.x + offsetOfServo) / armLength + 0.5));
      return true;
    }
    log.trace("rotate calc");
    double armL = armLength;
    Stream<Pair<Servo, Optional<Double>>> streamY, streamX;
    {
      // y軸モーター
      double mizoDY = size.y / 2 - railPosition; // 中心から下ミゾまでの長さ
      // 溝の直線の式
      final double m = Math.tan(angle);
      final double n = -mizoDY / Math.cos(angle) - m * position.x + position.y;
      streamY = Stream.of(servoY1, servoY2).map(s -> new Pair<>(s, s.getNewAngleFromLine(armL, m, n)));
    }
    {
      // x軸モーター
      double mizoDX = size.x / 2 - railPosition; // 中心から横ミゾまでの長さ
      // 溝の直線の式
      final double m = Math.tan(angle + Math.PI / 2);
      final double n = -mizoDX / Math.sin(angle) - m * position.x + position.y;
      streamX = Stream.of(servoX).map(s -> new Pair<>(s, s.getNewAngleFromLine(armL, m, n).map(a -> a + Math.PI / 2)));
    }
    List<Pair<Servo, Optional<Double>>> candidates = Stream.of(streamX, streamY).flatMap(s -> s)
        .collect(Collectors.toList());
    if (!candidates.stream().allMatch(p -> p.getValue().isPresent())) {
      log.warn("out of movable range");
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
   * @return {レイヤ座標, {レイヤ傾き, [Y軸サーボ角度(左), Y軸サーボ角度(右), X軸サーボ角度,]}}
   */
  public Pair<Vector, Pair<Double, List<Double>>> get() {
    return new Pair<>(this.position,
        new Pair<>(this.angle, Stream.of(servoY1, servoY2, servoX).map(Servo::getAngle).collect(Collectors.toList())));
  }

  /**
   * サーボのデフォルトアングル（オフセット）を取得します
   * 
   * @return オフセットリスト
   */
  public List<Pair<String, Double>> getServoDefaultAngles() {
    Function<Object, String> getFieldName = o -> {
      try {
        return Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> {
          try {
            f.setAccessible(true);
            return f.get(this) == o;
          } catch (Exception e) {
            return false;
          }
        }).findAny().map(f -> f.getName()).orElse("");
      } catch (Exception e) {
        return "";
      }
    };

    return Stream.of(servoY1, servoY2, servoX).map(s -> new Pair<>(getFieldName.apply(s), s.getDefaultAngle()))
        .collect(Collectors.toList());
  }

  /**
   * サーボのデフォルトアングル（オフセット）を設定します
   * 
   * @param y1
   * @param y2
   * @param x
   */
  public void setServoDefaultAngles(double y1, double y2, double x) {
    servoY1.setDefaultAngle(y1);
    servoY2.setDefaultAngle(y2);
    servoX.setDefaultAngle(x);
  }

  /**
   * サーボのアングルを補正なしで設定します
   * 
   * @param y1
   * @param y2
   * @param x
   */
  public void setServoAngles(double y1, double y2, double x) {
    servoY1.setAngle(y1 - servoY1.getDefaultAngle());
    servoY2.setAngle(y2 - servoY2.getDefaultAngle());
    servoX.setAngle(x - servoX.getDefaultAngle());
  }

  /**
   * レイヤ情報のOSCメッセージを作成します
   *
   * @return レイヤ情報OSCメッセージ
   */
  public LayerInfoOscMessage getInfoOscMessage() {
    return new LayerInfoOscMessage(size, (float) distanceOfServoY, (float) railPosition, (float) armLength,
        (float) offsetOfServo, (float) servoXsY, movementConstraints);
  }

  /**
   * レイヤのポジションを設定します。<br>
   * 角度は-15度から15度を-1から1に変換したものとなり、平行移動量はその角度での移動可能な量を-1から1にスケールした値で指定します
   * 
   * @param x
   * @param y
   * @param angle
   */
  public void setScaledPosition(Float x, Float y, Float angle) {
    Optional.of(this.movementConstraints.mapScaledPosition(x, y, angle))
        .ifPresent(p -> this.set(p.getKey(), p.getValue()));
  }

  /**
   * サーボのデフォルトアングルを設定します
   * 
   * @param offset サーボオフセット情報
   */
  public void setServoOffset(ServoOffset offset) {
    try {
      Optional.ofNullable(this.getClass().getDeclaredField(offset.servo)).map(f -> {
        try {
          f.setAccessible(true);
          return f.get(this);
        } catch (Exception e) {
          return null;
        }
      }).filter(Servo.class::isInstance).map(Servo.class::cast).ifPresent(s -> s.setDefaultAngle(offset.defautAngle));
    } catch (Exception e) {
      log.warn("cannot set servo offset.", e);
    }
  }

  /** サーボの回転軸座標をセットします */
  private void setServoPosition() {
    double servoYRail = -size.y / 2 + railPosition; // 高さレールの座標
    double servoY = servoYRail - armLength / 2 - offsetOfServo; // 高さサーボの回転軸高さ
    double servoXRail = servoYRail; // 横レールの座標

    servoY1.setPosition(new Vector(-distanceOfServoY / 2, servoY));
    servoY2.setPosition(new Vector(distanceOfServoY / 2, servoY));
    servoX.setPosition(new Vector(servoXRail - armLength / 2 - offsetOfServo, servoXsY));
  }
}
