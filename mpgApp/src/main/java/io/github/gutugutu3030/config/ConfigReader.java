package io.github.gutugutu3030.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import io.github.gutugutu3030.mpg.config.Config;

public class ConfigReader {
  private static Logger log = LoggerFactory.getLogger(ConfigReader.class);

  public static Optional<AbstractConfig> readConfig(Path path) {
    try (final InputStream in = Files.newInputStream(path)) {
      log.info("read");
      return Optional.of(new Yaml().loadAs(in, Config.class).setPath(path));
    } catch (Exception e) {
      log.error("config read error:{}", e.toString());
      return Optional.empty();
    }
  }
}
