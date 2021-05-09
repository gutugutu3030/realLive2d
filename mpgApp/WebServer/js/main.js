$(function () {
  $.extend($, {
    dmx: Array(512).fill(0),
    sendToServer: function (addr, args) {
      try {
        send(addr, args);
      } catch (e) {}
    },
    sendBundleToServer: function (bundle) {
      try {
        sendBundle(bundle);
      } catch (e) {}
    },
  });

  function setActiveBackground(isActive) {
    $("body").animate(
      {
        backgroundColor: isActive ? "#FFFFFF" : "#808080",
      },
      500
    );
  }

  var ws;

  /**
   * Websocketを開始します
   */
  function connectWebSocket() {
    ws = new WebSocket("ws://localhost:8080/gutugutu/art");

    ws.onopen = () => {
      setActiveBackground(true);
      sendBundle([
        { address: "/request/dmx", args: [] },
        { address: "/request/lightConfig", args: [] },
      ]);
    };

    ws.onmessage = (receive) => {
      var json = JSON.parse(receive.data);
      if (Array.isArray(json)) {
        //bundle
        json.forEach((mes) => {
          if ("address" in mes && "args" in mes) {
            received(mes.address, mes.args);
          } else {
            console.log("received illigal websocket packet.");
          }
        });
      }
      if ("address" in json && "args" in json) {
        received(json.address, json.args);
      } else {
        console.log("received illigal websocket packet.");
      }
    };
    ws.onclose = () => {
      setActiveBackground(false);
      setTimeout(connectWebSocket, 3000);
    };
    ws.error = (event) => {
      console.error(event.message);
      ws.close();
    };
  }

  connectWebSocket();
  setActiveBackground(false);

  /**
   * OSCアドレスとその実行関数のマップ
   */
  var commands = null;

  /**
   * websocketを受け取ります
   * @param {string} address
   * @param {Array} args
   */
  function received(address, args) {
    if (!commands) {
      commands = new Map(
        new Array( //
          responseDMX, //
          console.log, //
          $.lightConfig // map.js
        ).map((f) => ["/" + f.name, f])
      );
    }
    commands.get(address)(args);
  }
  /**
   * DMXの値を送信します
   * @param {Array} dmx dmx値
   */
  function sendDMX(args) {
    send("/set/dmx", args);
  }

  var $dmxSlider = [...Array(512)].map((_, i) => ({
    tag: $("*#dmxTag" + i),
    val: $("*#dmxVal" + i),
  }));

  /**
   * DMXタグの数値を設定します
   * @param {JQueryObject} $tag
   * @param {int} x
   */
  function setDMXTag($tag, x) {
    $tag.text("  " + x);
  }

  /**
   * DMXの値を受け取ります
   * @param {Array} args
   */
  function responseDMX(args) {
    var l = args[0].length;
    for (var i = 0; i < l; i++) {
      var x = args[0].charCodeAt(i);
      setDMXTag($dmxSlider[i].tag, x);
      $dmxSlider[i].val.val(x);
      $.dmx[i] = x;
    }
  }

  /**
   * websocketで送信します
   * @param {string} address アドレス
   * @param {Array} args 引数配列
   */
  function send(address, args) {
    ws.send(
      JSON.stringify({
        address: address,
        args: args,
      })
    );
  }

  /**
   * websocketでバンドルを送信します
   * @param {Array} bundle アドレスと引数配列のリスト
   */
  function sendBundle(bundle) {
    if (!Array.isArray(bundle)) {
      return;
    }
    ws.send(
      JSON.stringify(
        bundle
          .filter((m) => "address" in m && "args" in m)
          .map((m) => ({
            address: m.address,
            args: m.args,
          }))
      )
    );
  }

  // スライダリスナ登録
  Array(512)
    .fill()
    .map((_, i) => i) //
    .forEach((i) => {
      var $self = $("#dmxVal" + i);
      $self.val(0);
      $self.on("input", function () {
        // スライダからの入力時
        $.dmx[i] = parseInt($self.val());
        setDMXTag($dmxSlider[i].tag, $.dmx[i]);
        sendDMX($.dmx);
      });
    });
});
