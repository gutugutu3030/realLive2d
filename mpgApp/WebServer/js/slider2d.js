$(function() {
    $.extend($, {
        slider2d: [
            { x: 0, y: 0, layer: [] },
            { x: -1, y: -1, layer: [] },
            { x: 1, y: -1, layer: [] },
            { x: 1, y: 1, layer: [] },
            { x: -1, y: 1, layer: [] },
        ],
        slider2dInstance: function(p) {
            p.setup = () => {
                p.createCanvas(600, 400).parent("slider2d");
            };

            p.windowResized = () => {
                var $canvas = $("#slider2d");
                p.resizeCanvas($canvas.width(), ($canvas.width() * 2) / 3);
                rectSize[0] = p.width * 0.1;
                rectSize[1] = p.height * 0.1;
                rectSize[2] = p.width - rectSize[0] * 2;
                rectSize[3] = p.height - rectSize[1] * 2;
            };

            var firstDraw = true;

            var rectSize = [0, 0, 0, 0];

            var refPointR = 10;

            var nowPoint = null;

            var getRefPXY = (xy) =>
                new p.createVector(
                    (xy.x * rectSize[2]) / 2 + p.width / 2,
                    (xy.y * rectSize[3]) / 2 + p.height / 2
                );

            p.draw = () => {
                if (firstDraw) {
                    p.windowResized();
                    firstDraw = false;
                }
                p.background(255);
                p.fill(255);
                p.stroke(0);
                p.rect(rectSize[0], rectSize[1], rectSize[2], rectSize[3]);

                $.slider2d.forEach((e) => {
                    p.stroke(0);
                    if (e === targetRefPoint) {
                        p.fill(255, 0, 0);
                    } else {
                        p.fill(255);
                    }
                    var point = getRefPXY(e);
                    p.ellipse(point.x, point.y, refPointR, refPointR);
                });

                if (nowPoint != null) {
                    p.fill(150);
                    var point = getRefPXY(nowPoint);
                    p.ellipse(point.x, point.y, refPointR, refPointR);

                    console.log(
                        parseInt(
                            p.createVector(1, 1).angleBetween(nowPoint) / p.HALF_PI + 2
                        )
                    );
                }
            };

            p.mousePressed = () => {
                if (!ifin()) {
                    return;
                }
                if (p.mouseButton == "right") {
                    var mouse = p.createVector(p.mouseX, p.mouseY);
                    targetRefPoint = $.slider2d.find(
                        (e) => getRefPXY(e).dist(mouse) < refPointR
                    );

                    return;
                }
            };

            p.mouseDragged = () => {
                if (!ifin()) {
                    return;
                }
                if (p.mouseButton == "left") {
                    nowPoint = p.createVector(
                        ((p.mouseX - p.width / 2) / rectSize[2]) * 2,
                        ((p.mouseY - p.height / 2) / rectSize[3]) * 2
                    );
                }
            };

            p.mouseReleased = () => {
                nowPoint = null;
            };

            /**
             * 指定座標がキャンバス内か調べます
             * @param {int} x 指定しない場合はマウスX
             * @param {int} y 指定しない場合はマウスY
             */
            function ifin(x = p.mouseX, y = p.mouseY) {
                return 0 <= x && x < p.width && 0 <= y && y < p.height;
            }
        },
        updateScaledPosition: (array) => {
            if (targetRefPoint != null) {
                targetRefPoint.layer = array;
                return true;
            }
            return false;
        },
    });
    var targetRefPoint = null;

    const slider2dP5 = new p5($.slider2dInstance);
});