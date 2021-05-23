package io.github.gutugutu3030.mpg.layer.servo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"layer", "servo", "defautAngle"})
public class ServoOffset {
  /** 所属レイヤ */
  @JsonProperty("layer")
  public int layer;
  /** どのサーボか */
  @JsonProperty("servo")
  public String servo;
  /** オフセットアングル */
  @JsonProperty("defautAngle")
  public double defautAngle;

  public ServoOffset() {}

  public ServoOffset(int layer, String servo, double defaultAngle) {
    this.layer = layer;
    this.servo = servo;
    this.defautAngle = defaultAngle;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format(
        "%s[layer=%d,servo=%s,defaultAngle=%.3f]",
        this.getClass().getName(), this.layer, this.servo, this.defautAngle);
  }
}
