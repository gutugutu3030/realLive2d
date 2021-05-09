package io.github.gutugutu3030.config;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbstractConfig {
  public Path path;

  public AbstractConfig() {
    // orElseGetNewInstance();
  }

  public AbstractConfig setPath(Path path) {
    this.path = path;
    return this;
  }

  public String toString() {
    Object self = this;
    return String.format("%s[%s]", this.getClass().getName(), //
        Arrays.stream(getClass().getDeclaredFields()) //
            .map(f -> {
              try {
                Object data = f.get(self);
                if (data.getClass().isArray()) {
                  return f.getName() + "=" + Arrays.deepToString(Arrays.asList(data).toArray());
                }
                return f.getName() + "=" + f.get(self).toString();
              } catch (Exception e) {
                return null;
              }
            }).filter(Objects::nonNull).collect(Collectors.joining(",")));
  }
}
