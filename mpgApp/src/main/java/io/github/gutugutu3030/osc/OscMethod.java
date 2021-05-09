package io.github.gutugutu3030.osc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** ウェブソケットに対応付けられたことを示すアノテーションです */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OscMethod {
  /**
   * アドレスを指定します
   *
   * @return 対応付けられたアドレス
   */
  String addr();

  /**
   * 使用可能な通信方式を指定します
   *
   * @return 使用可能な通信方式
   */
  OscMethodType[] using() default {OscMethodType.OSC, OscMethodType.WEBSOCKET};
}
