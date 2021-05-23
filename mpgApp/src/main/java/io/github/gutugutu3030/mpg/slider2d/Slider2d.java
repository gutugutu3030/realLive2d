package io.github.gutugutu3030.mpg.slider2d;

import io.github.gutugutu3030.mpg.config.Config;
import io.github.gutugutu3030.util.Pair;
import io.github.gutugutu3030.util.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Slider2dの保管と補間を行います */
public class Slider2d {

  /** ロガー */
  Logger log = LoggerFactory.getLogger(this.getClass());

  /** 2次元補間データ */
  List<Slider2dData> data;
  /** jsonで起動時に読み込んだ2次元補間データ */
  List<Slider2dData> defaultData;
  /** レイヤの数 */
  int layerLength;

  /** ガウス分布のレート */
  double rate;

  /** @param data */
  public Slider2d(Config config) {
    this.data = config.interpolation.getDefaultSettingData(config.path).orElse(null);
    this.defaultData = data;
    final Comparator<Integer> comp = (s1, s2) -> Integer.compare(s1, s2);
    layerLength =
        Optional.ofNullable(data)
            .flatMap(l -> l.stream().map(s -> s.getLayer().size()).max(comp))
            .orElse(0);
    rate = config.interpolation.rate;
  }

  public List<Double> getData(double x, double y) {
    if (data == null) {
      return List.of();
    }
    Vector target = new Vector(x, y);
    List<Pair<List<Double>, Double>> gauss =
        data.stream()
            .map(d -> new Pair<>(d.getLayer(), new Vector(d.getX(), d.getY())))
            .map(p -> new Pair<>(p.getKey(), getGauss(rate * p.getValue().dist(target))))
            .collect(Collectors.toList());
    double[] sum = new double[this.layerLength];
    gauss.stream()
        .forEach(p -> IntStream.range(0, p.getKey().size()).forEach(i -> sum[i] += p.getValue()));
    double[] result = new double[this.layerLength];
    gauss.stream()
        .forEach(
            p ->
                IntStream.range(0, p.getKey().size())
                    .forEach(i -> result[i] += p.getKey().get(i) * p.getValue() / sum[i]));

    return Arrays.stream(result).boxed().collect(Collectors.toList());
  }

  /**
   * データを設定します
   *
   * @param data
   */
  public void set(List<Slider2dData> data) {
    this.data = data;
    final Comparator<Integer> comp = (s1, s2) -> Integer.compare(s1, s2);
    layerLength =
        Optional.ofNullable(data)
            .flatMap(l -> l.stream().map(s -> s.getLayer().size()).max(comp))
            .orElse(0);
    log.debug("set slider2d:{}", data);
  }

  /** 起動時にデータを戻します */
  public void reset() {
    this.set(defaultData);
  }

  private double getGauss(double x) {
    return (1 / Math.sqrt(Math.PI * 2)) * (Math.exp(-x * x));
  }
}
