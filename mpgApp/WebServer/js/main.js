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
        ws = new WebSocket("ws://" + location.hostname + ":8080/gutugutu/art");

        ws.onopen = () => {
            setActiveBackground(true);
            sendBundle([
                { address: "/get/info", type: "", args: [] },
                { address: "/getServoDefaultAngles", type: "", args: [] },
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
                    setLayersInfo, //
                    setServoDefaultAngles, //
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
        setLayerControlSlider(config);
    }

    function setLayerControlSlider(config) {
        var $layerDropDown = $("#layer-select-list");
        $layerDropDown.empty();
        var $layerControl = $("#layer-control");
        $layerControl.empty();
        config.map((c, i) => {
            // スライダの表示
            var $div = $("<div>").addClass("row");
            $("<div>")
                .addClass("col-12")
                .append($("<label>").text("Layer " + i))
                .appendTo($div);
            ["X", "Y", "ANGLE"].forEach((sliderName) => {
                $("<div>")
                    .addClass("col-12")
                    .append($("<label>").text(sliderName))
                    .appendTo($div);
                var $input = $("<input data-rangeSlider>")
                    .attr({
                        type: "range",
                        style: "width:100%;",
                        min: -1.0,
                        max: +1.0,
                        step: 0.001,
                    })
                    .val(0);
                $input.on("input", function() {
                    var list = $.getLayerScaledPositionFromSlider();
                    var bundle = [{
                        address: "/setLayerScaled",
                        type: list.map((e) => "f").join(""),
                        args: list,
                    }, ];
                    if ($.updateScaledPosition(list)) {
                        var slider2d = $.slider2d.flatMap((e) => [e.x, e.y, e.layer.length].concat(e.layer));
                        slider2d.unshift($.slider2d.length);
                        console.log(slider2d);
                        bundle.push({
                            address: "/setSlider2d",
                            type: slider2d.map((e) => "f").join(""),
                            args: slider2d,
                        });
                    }
                    $.sendBundleToServer(bundle);
                });
                $("<div>").addClass("col-12").append($input).appendTo($div);
            });
            $layerControl.append($div);
            if (i != 0) {
                $div.hide();
            }
            $div.addClass("layerDropdownDiv");
            $div.css("width", "100%");
            $div.data({ layer: i });
            // ドロップダウンリスト
            $layerDropDown.append(
                $("<a>")
                .attr({
                    class: "dropdown-item",
                    href: "#",
                })
                .text("layer " + i)
                .data({ layer: i })
                .click(function() {
                    $layerControl.find(".layerDropdownDiv").hide();
                    $div.show();
                })
            );
            $layerControl.append();
        });
    }

    function setServoDefaultAngles(args) {
        var offset = [];
        for (var i = 0; i < args.length / 3; i++) {
            offset.push({
                Y1: args[i * 3],
                Y2: args[i * 3 + 1],
                X: args[i * 3 + 2],
            });
        }

        var $servoOffset = $("#servo-offset-slider");
        $servoOffset.empty();
        offset.map((c, i) => {
            // スライダの表示
            var $div = $("<div>").addClass("row");
            $("<div>")
                .addClass("col-12")
                .append($("<label>").text("Layer " + i))
                .appendTo($div);
            ["Y1", "Y2", "X"].forEach((sliderName) => {
                $("<div>")
                    .addClass("col-2")
                    .append($("<label>").text(sliderName))
                    .appendTo($div);
                var $input = $("<input data-rangeSlider>")
                    .attr({
                        class: "col-10",
                        type: "range",
                        style: "width:100%;",
                        min: 0,
                        max: Math.PI / 10,
                        step: 0.001,
                    })
                    .val(c[sliderName]);
                $input
                    .on("input", function() {
                        var data = $("#servo-offset-slider input")
                            .get()
                            .map((e) => parseFloat($(e).val()));
                        $.sendToServer(
                            "/setServoAngles",
                            data.map((e) => "f").join(""),
                            data
                        );
                    })
                    .appendTo($div);
            });
            $servoOffset.append($div);
            $div.css("width", "100%");
            $div.data({ layer: i });
        });
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

    $("#set-servo-offset").on("click", function() {
        var data = $("#servo-offset-slider input")
            .get()
            .map((e) => parseFloat($(e).val()));
        $.sendToServer(
            "/setServoDefaultAngles",
            data.map((e) => "f").join(""),
            data
        );
    });

    $("#reset-servo-offset").on("click", function() {
        $.sendToServer("/getServoDefaultAngles", "", []);
    });

    $("#save-servo-offset").on("click", function() {
        if (confirm("今までの数値が削除されます。本当によろしいですか？")) {
            var data = $("#servo-offset-slider input")
                .get()
                .map((e) => parseFloat($(e).val()));
            $.sendBundleToServer([{
                    address: "/setServoDefaultAngles",
                    type: data.map((e) => "f").join(""),
                    args: data,
                },
                {
                    address: "/saveServoDefaultAngles",
                    type: "",
                    args: [],
                },
            ]);
        }
    });
});