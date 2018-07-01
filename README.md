# realLive2d
## reallive2dSimulator
Processing　設計ソフト＋シミュレータ

縮尺は 1pixel = 0.1mm
update.pdeのupdate関数内にpanelposをいじるコードを書くと良さげ
フレームレートが高いせいか，シリアルのコードがまずいのか，わからないが高確率でサーボがバグる

使用ライブラリ：controlP5

## Servo16
Arduino実行ファイル．上記の理由に付き基本的にはこっちをいじったほうが良い
こっちはstandalone関数内に書くと良さげ

使用ライブラリ：秋月電子のPCA9685.zip

## 3dmodels
使用した3Dモデル一覧
| name | 説明 |
----|----
| hone.stl | サーボモータのホーン拡張用 |
| servoHone.stl | hone.stlの元データ |
| arm.stl | ダミーサーボ　手前のレイヤーを分けるプラバン保持用　なお元データはPCがクラッシュして消えた |