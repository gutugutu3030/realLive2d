$(function() {
    $.extend($, {
        movementConstraints: { a: -1.6, b: 40 },
        commonPosition: { x: 0, y: 0, angle: 0 },
        /**
         *
         * @param {*} newPosition x,yは-1 - 1, angleはrad
         */
        setCommonPosition: function(newPosition) {
            if ("angle" in newPosition) {
                $.commonPosition.angle = newPosition.angle;
            }
            if ("x" in newPosition && "y" in newPosition) {
                var magnitude = Math.sqrt(
                    newPosition.x * newPosition.x + newPosition.y * newPosition.y
                );
                var per = Math.min(1, magnitude);
                var maxMagnitude =
                    $.movementConstraints.a * $.commonPosition.angle +
                    $.movementConstraints.b;
                $.commonPosition.x = ((newPosition.x * maxMagnitude) / magnitude) * per;
                $.commonPosition.y = ((newPosition.y * maxMagnitude) / magnitude) * per;
            }
            $("#position-p").text(
                "Common: (" +
                $.commonPosition.x +
                "," +
                $.commonPosition.y +
                ") rotate=" +
                $.commonPosition.angle
            );
            send("/setCommonPosition", "fff", [
                $.commonPosition.x,
                $.commonPosition.y,
                $.commonPosition.angle,
            ]);
        },
        /**
         * レイヤーの位置をスライダから取得します
         */
        getLayerScaledPositionFromSlider: () =>
            $("#layer-control")
            .find("input")
            .get()
            .map((e) => parseFloat($(e).val())),
        /**
         * ウェブソケットをサーバに送信します
         * @param {*} addr アドレス
         * @param {*} type 引数タイプ
         * @param {*} args 引数
         */
        sendToServer: function(addr, type, args) {
            try {
                send(addr, type, args);
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
            sendBundle([{ address: "/get/info", type: "", args: [] }]);
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
                size: { x: args[i * 9], y: args[i * 9 + 1] },
                distanceOfServoY: args[i * 9 + 2],
                railPosition: args[i * 9 + 3],
                armLength: args[i * 9 + 4],
                offsetOfServo: args[i * 9 + 5],
                servoXsY: args[i * 9 + 6],
            });
        }
        $.setLayerSettings(config);
    }

    /**
     * websocketで送信します
     * @param {string} address アドレス
     * @param {Array} args 引数配列
     */
    function send(address, type, args) {
        ws.send(
            JSON.stringify({
                address: address,
                type: type,
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
                .filter((m) => "address" in m && "args" in m && "type" in m)
                .map((m) => ({
                    address: m.address,
                    args: m.args,
                    type: m.type,
                }))
            )
        );
    }

    $("#slider-angle").on("input", function() {
        $.setCommonPosition({ angle: parseFloat($(this).val()) });
        $("#tag-angle").text($(this).val());
    });

    $("#save-layer-slider2d").on("click", function() {
        const blob = new Blob([JSON.stringify($.slider2d, null, "  ")], {
            type: "application/json",
        });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = "slider2d.json";
        link.click();
        URL.revokeObjectURL(url);
    });

    $("#load-layer-slider2d").on("click", function() {
        $("#load-layer-slider2d-input").click();
    });

    $("#load-layer-slider2d-input").on("change", function(e) {
        var reader = new FileReader();
        reader.onload = function(event) {
            $.slider2d = JSON.parse(event.target.result);
            var slider2d = $.slider2d.flatMap((e) => [e.x, e.y, e.layer.length].concat(e.layer));
            slider2d.unshift($.slider2d.length);
            $.sendToServer(
                "/setSlider2d",
                slider2d.map((e) => "f").join(""),
                slider2d
            );
        };
        reader.readAsText(e.target.files[0]);
    });

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