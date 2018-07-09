#include <Wire.h>
#include <PCA9685.h>            //PCA9685用ヘッダーファイル（秋月電子通商作成）
#include <skywriter.h>


PCA9685 pwm = PCA9685(0x40);    //PCA9685のアドレス指定（アドレスジャンパ未接続時）

#define SERVOMIN 51            //最小パルス幅 (標準的なサーボパルスに設定)
#define SERVOMAX 255            //最大パルス幅 (標準的なサーボパルスに設定)
#define BUFFERSIZE 100
#define LAYER_LENGTH 4

int defaultAngle[16] = {20, 28, 28, 29, 19, 26, 35, 30, 25, 34, 37, 36, 22, 33, 32, 28};

//0 - 100
int layerX[LAYER_LENGTH]={50,50,50,50};
int layerY[LAYER_LENGTH]={50,50,50,50};


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
}

unsigned long lastReceivingTime = 0;

void loop() {
  if (Serial.available()) {
    byte buffer[BUFFERSIZE];
    int length = Serial.readBytesUntil(0, buffer, BUFFERSIZE);
    for (int i = 0; i < length; i++) {
      if (buffer[i] == 0) {
        break;
      }
//      int angle = buffer[i] - 1;
//      if (0 <= angle && angle < 90) {
//        servo_write(i, angle + defaultAngle[i]);
//      }
        if(i%2==0){
          layerX[i/2]=buffer[i]-1;
        }else{
          layerY[i/2]=buffer[i]-1;          
        }
        writeLayer();
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
