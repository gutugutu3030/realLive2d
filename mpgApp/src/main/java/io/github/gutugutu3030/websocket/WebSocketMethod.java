package io.github.gutugutu3030.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** ウェブソケットに対応付けられたことを示すアノテーションです */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebSocketMethod {
  String addr(); // 対応付けられたアドレス
}
