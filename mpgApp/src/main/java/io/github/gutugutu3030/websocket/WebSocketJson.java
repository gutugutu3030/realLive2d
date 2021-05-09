package io.github.gutugutu3030.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"address", "args"})
public class WebSocketJson {
  @JsonProperty("address")
  public String address;

  @JsonProperty("args")
  public List<?> args;

  public String toString() {
    return String.format("WebSocketJson[address=%s,args=%s]", address, args);
  }
}
