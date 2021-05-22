package io.github.gutugutu3030.mpg.config.interpolation;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.github.gutugutu3030.config.AbstractConfig;
import io.github.gutugutu3030.mpg.slider2d.Slider2dData;

public class InterpolationConfig extends AbstractConfig {
    /**
     * slider2dのデフォルト状態を記したjsonファイルのパス
     */
    public String defaultSetting;

    /**
     * ガウス分布のレート
     */
    public double rate = 1;

    public Optional<List<Slider2dData>> getDefaultSettingData(Path path) {
        if (path == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(defaultSetting).map(fileName -> {
            try {
                return new Gson().fromJson(Files.readString(path.getParent().resolve(fileName), StandardCharsets.UTF_8),
                        new TypeToken<Collection<Slider2dData>>() {
                        }.getType());
            } catch (IOException e) {
                return null;
            }
        });
    }
}
