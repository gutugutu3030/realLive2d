package io.github.gutugutu3030.mpg.slider2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.slf4j.LoggerFactory;

public class Slider2dData {

    public static Optional<List<Slider2dData>> parseFloatList(List<Float> data) {
        try {
            int index[] = { 1 };
            return Optional.of(IntStream.range(0, (int) (float) data.get(0)).mapToObj(i -> {
                double x = data.get(index[0]++);
                double y = data.get(index[0]++);
                List<Double> d = new ArrayList<>();
                for (int j = 0, m = (int) (float) data.get(index[0]++); j < m; j++) {
                    d.add((double) (float) data.get(index[0]++));
                }
                return new Slider2dData(x, y, d);
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            LoggerFactory.getLogger(Slider2dData.class).error("failed parse FloatList", e);
            return Optional.empty();
        }
    }

    @SerializedName("x")
    @Expose
    private double x;
    @SerializedName("y")
    @Expose
    private double y;
    @SerializedName("layer")
    @Expose
    private List<Double> layer = null;

    public Slider2dData() {

    }

    public Slider2dData(double x, double y, List<Double> layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public List<Double> getLayer() {
        return layer;
    }

    public void setLayer(List<Double> layer) {
        this.layer = layer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[(%.3f,%.3f) %s]", this.getClass().getName(), this.x, this.y, this.layer);
    }
}