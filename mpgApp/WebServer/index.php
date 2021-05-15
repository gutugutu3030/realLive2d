<?php
$title = "art";
?>
<!DOCTYPE html>
<html>
<head>
<title><?php echo $title; ?></title>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<script src="js/jquery-3.5.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/jquery.color.js"></script>
<script src="js/main.js"></script>
<script src="js/p5.min.js"></script>
<script src="js/map.js"></script>
<link rel="stylesheet" type="text/css" href="css/range-slider.min.css">
<script src="js/range-slider.min.js"></script>
</head>
<body>
<div class="m-5">
    <div class="row">
        <div class="col-sm-8" style="width:500px">
            <div class="bs-component">
                <ul class="nav nav-tabs">
                    <li class="nav-item">
                        <a class="nav-link active" data-toggle="tab" href="#map">Map</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-toggle="tab" href="#simple-command">Simple</a>
                    </li>
                </ul>
                <div id="main-tabs" class="tab-content">
                    <div class="tab-pane fade show active p-3" id="map">
                        <div class="btn-group btn-group-toggle mb-1" id="map-drag-option"  data-toggle="buttons">
                            <label class="btn btn-info active">
                            <input type="radio" name="options" autocomplete="off" value="0"> 回転
                            </label>
                            <label class="btn btn-info">
                            <input type="radio" name="options" autocomplete="off" value="1"> 移動
                            </label>
                            <label class="btn btn-info">
                            <input type="radio" name="options" autocomplete="off" value="2"> 拡大
                            </label>
                            <label class="btn btn-info">
                            <input type="radio" name="options" autocomplete="off" value="3"> 操作
                            </label>
                        </div>
                        <div id="map-canvas"></div>
                    </div>
                    <div class="tab-pane fade" id="simple-command">
                        <p>昔しの書生は、笈を負ひて四方に遊歴し、此人ならばと思ふ先生の許に落付く、故に先生を敬ふ事、父兄に過ぎたり、先生も亦弟子に対する事、真の子の如し、是でなくては真の教育といふ事は出来ぬなり、今の書生は学校を旅屋の如く思ふ、金を出して暫らく逗留するに過ぎず、厭になればすぐに宿を移す、かゝる生徒に対する校長は、宿屋の主人の如く、教師は番頭丁稚なり、主人たる校長すら、時には御客の機嫌を取らねばならず、況んや番頭丁稚をや、薫陶所か解雇されざるを以て幸福と思ふ位なり、生徒の増長し教員の下落するは当前の事なり。</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="bs-component">
                <ul class="nav nav-tabs">
                    <li class="nav-item">
                        <a class="nav-link active" data-toggle="tab" href="#info-sliders">DMX</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" data-toggle="tab" href="#info-art">Timeline</a>
                    </li>
                </ul>
                <div id="sub-tabs" class="tab-content">
                    <div class="tab-pane fade show active" id="info-sliders">
                        <div class="mt-3 overflow-auto">
                            <ol start="0">
                                <?php for ($i=0; $i<512; $i++) { ?>
                                <li>
                                    <div class="row">
                                        <div class="col-8">
                                            <input id="dmxVal<?php echo $i?>" type="range" style="width:100%;" min="0" max="255" step="1" data-rangeSlider title="">
                                        </div>                    
                                        <div class="col-2">
                                            <p id="dmxTag<?php echo $i?>">---</p>
                                        </div>
                                    </div>
                                </li>
                                <?php } ?>
                            </ol>
                        </div>
                    </div>
                    <div class="tab-pane fade" id="info-art">
                        <p>昔しの書生は、笈を負ひて四方に遊歴し、此人ならばと思ふ先生の許に落付く、故に先生を敬ふ事、父兄に過ぎたり、先生も亦弟子に対する事、真の子の如し、是でなくては真の教育といふ事は出来ぬなり、今の書生は学校を旅屋の如く思ふ、金を出して暫らく逗留するに過ぎず、厭になればすぐに宿を移す、かゝる生徒に対する校長は、宿屋の主人の如く、教師は番頭丁稚なり、主人たる校長すら、時には御客の機嫌を取らねばならず、況んや番頭丁稚をや、薫陶所か解雇されざるを以て幸福と思ふ位なり、生徒の増長し教員の下落するは当前の事なり。</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

</body>
</html>