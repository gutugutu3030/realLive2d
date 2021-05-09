package io.github.gutugutu3030.mpg.config.servo;

import io.github.gutugutu3030.config.AbstractConfig;

public class ServoConfig extends AbstractConfig {
    /**
     * pca9685のチャンネル
     */
    public int[] PCA9685Channels = new int[] { 0x40 };
    /**
     * アーム長さ
     */
    public double armLength = 110;
    /**
     * Y軸操作のサーボの回転軸同士の幅
     */
    public double distanceOfServoY = 230;
    /**
     * X軸操作のサーボの高さ
     */
    public double yOfServoX = 70;
    /**
     * 0で30度の位置が真ん中 数値を増やすとその分だけサーボがパネルから離れます
     */
    public double positionOffset = 10;
}
