$(function() {
    $.extend($, {
        mapInstance: function(p) {
            p.setup = () => {
                p.createCanvas(600, 400, p.WEBGL).parent("map-canvas");
                var $canvas = $("#map-canvas");
                p.resizeCanvas($canvas.width(), ($canvas.width() * 2) / 3);
                p.ortho(-p.width / 2, p.width / 2, p.height / 2, -p.height / 2, 0, 500);
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
                p.scale(1, 1, 1);
                p.scale(coordinate.scale);
                drawAxis();
                drawLayers();

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
                            coordinate.scale *= p.max(p.min(1 + p.movedY * speed, 1.05), 0.99);
                            break;
                        }
                    case "3":
                        {
                            $.setCommonPosition({
                                x: (p.mouseX / p.width - 0.5) * 2,
                                y: -(p.mouseY / p.height - 0.5) * 2,
                            });
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
                p.strokeWeight(2);
                p.stroke(255, 0, 0);
                p.line(0, 0, 0, l, 0, 0);
                p.stroke(0, 255, 0);
                p.line(0, 0, 0, 0, l, 0);
                p.stroke(0, 0, 255);
                p.line(0, 0, 0, 0, 0, l);
                p.strokeWeight(1);
            }

            function drawLayers() {
                layers.forEach((l, i) => {
                    p.push();
                    l.draw(p);
                    p.pop();
                });
            }
        },

        setLayerSettings: function(layersConfig) {
            var $layerDropDown = $("#layer-select-list");
            $layerDropDown.empty();
            var $layerControl = $("#layer-control");
            $layerControl.empty();
            layers = layersConfig.map((c, i) => {
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
                        var list = $("#layer-control")
                            .find("input")
                            .get()
                            .map((e) => parseFloat($(e).val()));
                        $.sendToServer(
                            "/setLayerScaled",
                            list.map((e) => "f").join(""),
                            list
                        );
                    });
                    $("<div>").addClass("col-12").append($input).appendTo($div);
                });
                $layerControl.append($div);
                // $div.hide();
                $div.addClass("layerDropdownDiv");
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
                        var layer = $(this).data("layer");
                        console.log(layer);
                        $layerControl.find(".layerDropdownDiv").hide();
                        $div.show();
                        // $layerControl
                        //     .find(".layerDropdownDiv")
                        //     .get()
                        //     .map((e) => $(e))
                        //     .filter((e) => e.data("layer") == layer)
                        //     .forEach((e) => e.show());
                    })
                );

                $layerControl.append();
                return new Layer(
                    c, { x: 0, y: 0, z: i * layerDistance, isReverse: i % 2 == 1 },
                    mapP5.loadImage("data/testSet/" + (6 - i) + ".png")
                );
            });
        },

        setLayerPosition: function(positions) {
            for (var i = 0; i < positions.length / 6; i++) {
                if (layers.length > i) {
                    layers[i].setPosition({
                        x: positions[i * 6],
                        y: positions[i * 6 + 1],
                        rotate: positions[i * 6 + 2],
                    });
                    layers[i].setServoAngles([
                        positions[i * 6 + 3],
                        positions[i * 6 + 4],
                        positions[i * 6 + 5],
                    ]);
                }
            }
        },

        setServoAngles: function(angles) {
            for (var i = 0; i < angles.length / 3; i++) {
                if (layers.length > i) {
                    layers[i].setServoAngles(
                        angles[i * 3],
                        angles[i * 3 + 1],
                        angles[i * 3 + 2]
                    );
                }
            }
        },
    });

    var layerDistance = 25;

    var layers = [];

    function test() {
        console.log("呼び出せる");
    }

    const mapP5 = new p5($.mapInstance);
});

var servoSize = { w: 22.2, h: 11.8, d: 40 };

class Layer {
    // constructor(pos) {
    //     this.servoAngles = [0, 0, 0];
    //     this.size = { x: 250, y: 250 };
    //     this.distanceOfServoY = 230;
    //     this.railPosition = 10;
    //     this.armLength = 110;
    //     this.offsetOfServo = 10;
    //     this.servoXsY = 70;
    //     //
    //     this.position = pos;
    //     this.rotate = 0;
    // }
    constructor(config, pos, image = null) {
        this.servoAngles = [0, 0, 0];
        this.size = config.size;
        this.distanceOfServoY = config.distanceOfServoY;
        this.railPosition = config.railPosition;
        this.armLength = config.armLength;
        this.offsetOfServo = config.offsetOfServo;
        this.servoXsY = config.servoXsY;
        //
        this.position = pos;
        this.rotate = 0;

        this.image = image;
    }
    setPosition(position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.rotate = position.rotate;
    }
    setServoAngles(servoAngles) {
        this.servoAngles = servoAngles;
    }
    draw(p) {
        var servoYRail = -this.size.y / 2 + this.railPosition; //高さレールの座標
        var servoY = servoYRail - this.armLength / 2 - this.offsetOfServo; //高さサーボの回転軸高さ
        var servoXRail = -this.size.x / 2 + this.railPosition; //横レールの座標
        var servoPosition = [
            { x: -this.distanceOfServoY / 2, y: servoY },
            { x: this.distanceOfServoY / 2, y: servoY },
            {
                x: servoXRail - this.armLength / 2 - this.offsetOfServo,
                y: this.servoXsY,
            },
        ];
        p.noFill();
        p.stroke(0);
        p.push(); {
            if (this.position.isReverse) {
                p.rotateZ(p.PI);
            }

            p.translate(0, 0, this.position.z);
            p.push(); {
                p.translate(this.position.x, this.position.y);
                p.rotateZ(this.rotate);
                p.rect(-this.size.x / 2, -this.size.y / 2, this.size.x, this.size.y);
                if (this.image != null) {
                    p.push();
                    if (this.position.isReverse) {
                        p.rotateZ(p.PI);
                    }
                    p.scale(1, -1, 1);
                    p.image(
                        this.image, -this.size.x / 2, -this.size.y / 2,
                        this.size.x,
                        this.size.y
                    );
                    p.pop();
                }
                p.line(-this.size.x / 2 + 10,
                    servoYRail,
                    this.size.x / 2 - 10,
                    servoYRail
                );
                p.line(
                    servoXRail, -this.size.y / 2 + this.railPosition,
                    servoXRail,
                    this.size.y / 2 - this.railPosition
                );
            }
            p.pop();
            servoPosition.forEach((pos, i) =>
                this.drawServo(p, i, pos, this.servoAngles[i])
            );
        }
        p.pop();
    }
    drawServo(p, i, position, angle) {
        p.fill(100, 100, 255);
        p.push();
        p.translate(position.x, position.y);
        if (i == 2) {
            p.rotateZ(-Math.PI / 2);
        }
        p.stroke(0);
        p.strokeWeight(1);
        p.box(servoSize.w, servoSize.h, servoSize.d);
        p.line(
            0,
            0,
            p.cos(-angle) * this.armLength, -p.sin(-angle) * this.armLength
        );
        p.stroke(255, 0, 0);
        p.strokeWeight(0.5);
        p.line(0, 0, this.armLength, 0);
        p.pop();
    }
}