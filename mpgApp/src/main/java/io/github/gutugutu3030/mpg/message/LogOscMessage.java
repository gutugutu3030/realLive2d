package io.github.gutugutu3030.mpg.message;

import com.illposed.osc.OSCMessage;
import java.util.List;

public class LogOscMessage extends OSCMessage {
  /** */
  private static final long serialVersionUID = 1L;

  public LogOscMessage(String str) {
    super("/log", List.of(str));
  }
}
