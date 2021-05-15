$(function() {
    $.extend($, {
        dmx: Array(512).fill(0),
        sendToServer: function(addr, args) {
            try {
                send(addr, args);
            } catch (e) {}
        },
        sendBundleToServer: function(bundle) {
            try {
                sendBundle(bundle);
            } catch (e) {}
        },
    });

    function setActiveBackground(isActive) {
        $("body").animate({
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
            sendBundle([{ address: "/get/info", args: [] }]);
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
        console.log(address, args);
        if (!commands) {
            commands = new Map(
                new Array( //
                    setLayersInfo, //
                    console.log, //
                    $.setLayerPosition, // map.js
                    $.setServoAngles // map.js
                ).map((f) => ["/" + f.name, f])
            );
        }
        try {
            commands.get(address)(args);
        } catch (e) {
            console.error(e);
        }
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
     * レイヤの各種情報を設定します
     * @param {Array} args
     */
    function setLayersInfo(args) {
        var config = [];
        for (var i = 0; i < args.length / 9; i++) {
            config.push({
                size: { x: args[i * 8], y: args[i * 8 + 1] },
                distanceOfServoY: args[i * 8 + 2],
                railPosition: args[i * 8 + 3],
                armLength: args[i * 8 + 4],
                offsetOfServo: args[i * 8 + 5],
                servoXsY: args[i * 8 + 6],
            });
        }
        $.setLayerSettings(config);
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
            $self.on("input", function() {
                // スライダからの入力時
                $.dmx[i] = parseInt($self.val());
                setDMXTag($dmxSlider[i].tag, $.dmx[i]);
                sendDMX($.dmx);
            });
        });
});