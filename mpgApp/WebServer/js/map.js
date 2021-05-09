$(function() {
    $.extend($, {
        mapInstance: function(p) {
            p.setup = () => {
                p.createCanvas(600, 400, p.WEBGL).parent("map-canvas");
                var $canvas = $("#map-canvas");
                p.resizeCanvas($canvas.width(), ($canvas.width() * 2) / 3);
            };

            p.draw = () => {
                p.background(100);

                p.translate(
                    coordinate.translateX,
                    coordinate.translateY,
                    coordinate.translateZ
                );
                p.rotateX(coordinate.rotateX);
                p.rotateY(coordinate.rotateY);
                p.scale(coordinate.scale);
                drawAxis();
                drawLights();

                // $.sendToServer("/log/server", ["start p5js"]);
            };

            var coordinate = {
                rotateX: 0,
                rotateY: 0,
                translateX: 0,
                translateY: 0,
                translateZ: 0,
                scale: 1,
            };

            p.mouseDragged = () => {
                if (!ifin()) {
                    return;
                }
                switch ($("#map-drag-option > .btn.active > input").val()) {
                    case "0":
                        {
                            //座標系回転
                            var speed = 0.005;
                            coordinate.rotateX -= p.movedY * speed;
                            coordinate.rotateY -= p.movedX * speed;
                            break;
                        }
                    case "1":
                        {
                            //座標系移動
                            var speed = 0.1;
                            coordinate.translateX += p.movedX * speed;
                            coordinate.translateY += p.movedY * speed;
                            break;
                        }
                    case "2":
                        {
                            //座標系スケール
                            var speed = 0.002;
                            coordinate.scale *= p.max(p.min(1 + p.movedY * speed, 1.05), 0.95);
                            break;
                        }
                }
            };

            /**
             * 指定座標がキャンバス内か調べます
             * @param {int} x 指定しない場合はマウスX
             * @param {int} y 指定しない場合はマウスY
             */
            function ifin(x = p.mouseX, y = p.mouseY) {
                return 0 <= x && x < p.width && 0 <= y && y < p.height;
            }

            function drawAxis(l = 100) {
                p.strokeWeight(3);
                p.stroke(255, 0, 0);
                p.line(0, 0, 0, l, 0, 0);
                p.stroke(0, 255, 0);
                p.line(0, 0, 0, 0, l, 0);
                p.stroke(0, 0, 255);
                p.line(0, 0, 0, 0, 0, l);
                p.strokeWeight(1);
            }

            function drawLights() {
                p.noStroke();
                Object.keys(lights).forEach(function(key) {
                    p.push();
                    var light = this[key];
                    p.translate(light.x, light.y, light.z);
                    p.fill(
                        $.dmx[light.startCh],
                        $.dmx[light.startCh + 1],
                        $.dmx[light.startCh + 2]
                    );
                    p.box(10);
                    p.pop();
                }, lights);
            }
        },

        lightConfig: function(args) {
            if (!Array.isArray(args) || args.length != 4) {
                return;
            }
            lights[args[0]] = {
                startCh: parseInt(args[0]),
                x: parseFloat(args[1]),
                y: parseFloat(args[2]),
                z: parseFloat(args[3]),
            };
        },
    });

    var lights = {};

    function test() {
        console.log("呼び出せる");
    }

    const mapP5 = new p5($.mapInstance);
});