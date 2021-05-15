package io.github.gutugutu3030.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"address", "args"})
public class WebSocketJson {
  @JsonProperty("address")
  public String address;

  @JsonProperty("type")
  public String type;

  @JsonProperty("args")
  public List<?> args;

  /**
   * typeに記述されたオブジェクトに変換します
   *
   * @return 変換された引数リスト
   * @throws IllegalArgumentException 引数エラー
   */
  public List<Object> getParsedArgs() throws IllegalArgumentException {
    if (type == null) {
      if (args.isEmpty()) {
        return List.of();
      }
      throw new IllegalArgumentException("type is null");
    }
    return IntStream.range(0, Math.min(type.length(), args.size()))
        .mapToObj(
            i -> {
              Object target = args.get(i);
              char c = type.charAt(i);
              if (c == 's') {
                return String.valueOf(target);
              }
              if (!Number.class.isInstance(target)) {
                throw new IllegalArgumentException("unknown object type: " + target.getClass());
              }
              switch (c) {
                case 'i':
                  return ((Number) target).intValue();
                case 'f':
                case 'd':
                  return ((Number) target).floatValue();
              }
              throw new IllegalArgumentException("unknown object type: " + target.getClass());
            })
        .collect(Collectors.toList());
  }

  public String toString() {
    return String.format("WebSocketJson[address=%s,type=%s,args=%s]", address, type, args);
  }
}
