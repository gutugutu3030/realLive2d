# realLive2d
![gif](animation.gif)
## reallive2dSimulator
Processing　設計ソフト＋シミュレータ

縮尺は 1pixel = 0.1mm
一応シリアル通信機能はついているがv1用　現状のプログラムでは使用できないので注意

使用ライブラリ：controlP5

## Servo16
Arduino実行ファイル．上記の理由に付き基本的にはこっちをいじったほうが良い
こっちはstandalone関数内に書くと良さげ
usingEEPROMフラグでレイヤの移動量をEEPROMから読むかソースコードの値を参照するかを決定できる

使用ライブラリ：秋月電子のPCA9685.zip，[Skywriter Arduino Library](https://github.com/pimoroni/skywriter-hat/tree/master/arduino)，[SparkFun GridEYE Arduino Library](https://github.com/sparkfun/SparkFun_GridEYE_Arduino_Library), EEPROM

## explore
顔のパラメータを変更可能　上記のEEPROMモードにてパラメータ書き込みをするソフトもこれが担当する

マウス座標に顔が向くようになっている　上のスライダにて各レイヤの移動量を変更できる．移動量はキー操作でEEPROMに書き込みができる

## timeline
5秒間，各レイヤの動きを記録，再生できる
簡単なタイムラインインターフェースからなっている
sキーで保存，lキーで読み込みが可能

## deteilsaver
キャリブレーション用システム
Servo16_deteilsaverを書き込んだ状態で起動することで，サーボの角度のキャリブレーションができる

## DOASerial.py
python2.7必須
Arduinoのシリアルポートに設定することで聴覚を与えることができる
use [ReSpeaker USB 4 Mic Array](https://github.com/respeaker/usb_4_mic_array/)

## 3dmodels
使用した3Dモデル一覧

| name | 説明 |
----|----
| hone.stl | サーボモータのホーン拡張用 |
| servoHone.scad | hone.stlの元データ |
| line.scad | 4連サーボのデータ　サーボのモデルデータ込 |
| v2.scad | v2プレートのデータ　要line.scad |
| v2.stl | v2プレートのデータ |
| v2-arm.scad | セパレータ（透明の板）を保持するアームfor v2 の元データ|
| v2-arm.stl | セパレータを保持するアームfor v2|
