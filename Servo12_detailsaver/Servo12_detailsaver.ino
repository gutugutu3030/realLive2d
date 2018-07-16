#include <Wire.h>
#include <PCA9685.h>            //PCA9685用ヘッダーファイル（秋月電子通商作成）


PCA9685 pwm = PCA9685(0x40);    //PCA9685のアドレス指定（アドレスジャンパ未接続時）

#define SERVOMIN 51            //最小パルス幅 (標準的なサーボパルスに設定)
#define SERVOMAX 255            //最大パルス幅 (標準的なサーボパルスに設定)
#define BUFFERSIZE 100


//開始命令＋終了命令：0
//1~181までの数値を受け取る

void setup() {
  Serial.begin(57600);
  pwm.begin();                   //初期設定 (アドレス0x40用)
  pwm.setPWMFreq(20);            //PWM周期を60Hzに設定 (アドレス0x40用)
  delay(500);
  for (int i = 0; i < 12; i++) {
    servo_write(i, 30);
  }
}

void loop() {
  if (Serial.available()) {
    byte buffer[BUFFERSIZE];
    int length = Serial.readBytesUntil(0, buffer, BUFFERSIZE);
    if (length >= 12) {
      for (int i = 0; i < 12; i++) {
        servo_write(i, buffer[i] - 1);
      }
    }
  }
}


void servo_write(int ch, int ang) { //動かすサーボチャンネルと角度を指定
  ang = map(ang, 0, 180, SERVOMIN, SERVOMAX); //角度（0～180）をPWMのパルス幅（150～600）に変換
  pwm.setPWM(ch, 0, ang);
  //delay(1);
}
