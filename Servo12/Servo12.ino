#include <EEPROM.h>
#include <Wire.h>
#include <SparkFun_GridEYE_Arduino_Library.h>
#include <PCA9685.h>            //PCA9685用ヘッダーファイル（秋月電子通商作成）
#include <skywriter.h>

bool usingEEPROM = true;

PCA9685 pwm = PCA9685(0x40);    //PCA9685のアドレス指定（アドレスジャンパ未接続時）

GridEYE grideye;//サーモセンサ

#define SERVOMIN 51            //最小パルス幅 (標準的なサーボパルスに設定)
#define SERVOMAX 255            //最大パルス幅 (標準的なサーボパルスに設定)
#define BUFFERSIZE 100
#define LAYER_LENGTH 3
#define SERVO_NUM 12

int amount[LAYER_LENGTH] = {30, -22, 50}; //usingEEPROM=falseのときに使用される値
int defaultAngle[SERVO_NUM] = {24, 35, 25, 18, 16, 24, 30, 23, 22, 23, 0, 23/*, 26, 13, 25, 31*/};
//レイヤー2可動サーボが時々プラ版に引っかかるため，動作を制限する
int maxmin[LAYER_LENGTH][2] = {{95, 0}, {100, 15}, {100, 0}};


//0 - 100
int layerX[LAYER_LENGTH] = {50, 50, 50};
int layerY[LAYER_LENGTH] = {50, 50, 50};


//開始命令＋終了命令：0
//1~181までの数値を受け取る

void setup() {
  Serial.begin(57600);
  delay(500);
  pwm.begin();                   //初期設定 (アドレス0x40用)
  pwm.setPWMFreq(20);            //PWM周期を60Hzに設定 (アドレス0x40用)
  delay(500);
  Skywriter.begin(4, 5);
  Skywriter.onXYZ(handle_xyz);
  delay(50);
  grideye.begin();
  if (usingEEPROM) {
  Serial.println("layerAmount:");
    for (int i = 0; i < LAYER_LENGTH; i++) {
      amount[i] = EEPROM[i] - 50;
      Serial.print(amount[i]);
      Serial.print(" ");
    }
    Serial.println();
  }
}

unsigned long lastReceivingTime = 0;

void loop() {
  if (Serial.available()) {
    byte buffer[BUFFERSIZE];
    int length = Serial.readBytesUntil(0, buffer, BUFFERSIZE);
    if (length != 0) {
      if (exploreEvent(buffer[0], buffer + 1, length - 1)) {
        lastReceivingTime = millis();
      }
    }
  }
  if (millis() - lastReceivingTime > 3000) {
    standalone();
  }
}


void servo_write(int ch, int ang) { //動かすサーボチャンネルと角度を指定
  ang = map(ang, 0, 180, SERVOMIN, SERVOMAX); //角度（0～180）をPWMのパルス幅（150～600）に変換
  pwm.setPWM(ch, 0, ang);
  //delay(1);
}
