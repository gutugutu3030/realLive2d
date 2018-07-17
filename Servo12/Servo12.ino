#include <Wire.h>
#include <SparkFun_GridEYE_Arduino_Library.h>
#include <PCA9685.h>            //PCA9685用ヘッダーファイル（秋月電子通商作成）
#include <skywriter.h>


PCA9685 pwm = PCA9685(0x40);    //PCA9685のアドレス指定（アドレスジャンパ未接続時）

GridEYE grideye;//サーモセンサ

#define SERVOMIN 51            //最小パルス幅 (標準的なサーボパルスに設定)
#define SERVOMAX 255            //最大パルス幅 (標準的なサーボパルスに設定)
#define BUFFERSIZE 100
#define LAYER_LENGTH 3
#define SERVO_NUM 12

int defaultAngle[SERVO_NUM] = {24, 35, 25, 18, 16, 24, 30, 23, 22, 23, 0, 23/*, 26, 13, 25, 31*/};

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
}

unsigned long lastReceivingTime = 0;

void loop() {
  if (Serial.available()) {
    byte buffer[BUFFERSIZE];
    int length = Serial.readBytesUntil(0, buffer, BUFFERSIZE);
    if(length>=5){
      int x=buffer[0]-1;
      int y=buffer[1]-1;
      int layerAmount[3]={buffer[2]-51,buffer[3]-51,buffer[4]-51};
      setLayerXY(x/100.0-0.5,y/100.0-0.5,layerAmount);
    }
    lastReceivingTime = millis();
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
