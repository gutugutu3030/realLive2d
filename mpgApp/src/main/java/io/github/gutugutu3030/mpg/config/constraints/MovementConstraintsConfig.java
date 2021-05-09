package io.github.gutugutu3030.mpg.config.constraints;

import io.github.gutugutu3030.config.AbstractConfig;

/**
 * 傾きと平行移動の制約条件 [平行移動のMAX（mm）] = [傾き（度）] x a + b
 */
public class MovementConstraintsConfig extends AbstractConfig {
    public double a = -1.6;
    public double b = 40;
}
