package io.github.gutugutu3030.osc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** OscMethodを実行します */
public class OscExecuter {
  private static Logger log = LoggerFactory.getLogger(OscExecuter.class);

  /**
   * 指定された引数とアドレスにマッチするメソッドを探して呼び出します
   *
   * @param address アドレス
   * @param args 引数
   * @param methodClass メソッドが書かれたクラス
   * @param type 実行タイプ
   */
  public static void exec(
      String address, List<Object> args, Object methodClass, OscMethodType type) {
    Arrays.stream(methodClass.getClass().getDeclaredMethods())
        .filter(
            m ->
                Arrays.stream(m.getDeclaredAnnotations())
                    .filter(OscMethod.class::isInstance)
                    .map(OscMethod.class::cast)
                    .findAny() //
                    .filter(a -> Arrays.asList(a.using()).contains(type)) // 対応タイプかどうか
                    .map(OscMethod::addr)
                    .map(address::equals)
                    .orElse(false))
        .findAny()
        .ifPresent(
            m -> {
              List<Class<?>> parameterType =
                  Arrays.stream(m.getParameterTypes()).collect(Collectors.toList());
              try {
                if (parameterType.size() == 0) {
                  m.invoke(methodClass);
                  return;
                }
                if (parameterType.size() == 1
                    && List.class.isAssignableFrom(parameterType.get(0))) {
                  m.invoke(methodClass, args);
                  return;
                }
                m.invoke(methodClass, args.toArray());
              } catch (Exception e) {
                log.error("failed invoke method", e);
              }
            });
  }
}
