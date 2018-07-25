#include    <math.h>
#define WIDTHDE 5

void setLayerXY(float x, float y,int *layerAmount) {
  if (x < -0.5 || 0.5 < x)return;
  if (y < -0.5 || 0.5 < y)return;

  for (int i = 0; i < LAYER_LENGTH; i++) {
    layerX[i] = 50 + (int)(x * layerAmount[i]);
    layerY[i] = 50 + (int)(y * layerAmount[i]);
  }
  writeLayer();
}

void writeLayer() {
  if (LAYER_LENGTH * 4 > 16) {
    return;
  }
  for (int i = 0; i < LAYER_LENGTH; i++) {
    layerX[i]=min(90,max(10,layerX[i]));
    layerY[i]=min(90,max(10,layerY[i]));
    servo_write(i * 4, defaultAngle[i * 4] + (acos((layerX[i] + 100-WIDTHDE) / 300.0) * 180 / M_PI)); //upperLeft
    servo_write(i * 4 + 1, defaultAngle[i * 4 + 1] + (90 - acos(1 - (layerX[i] + 100+WIDTHDE) / 300.0) * 180 / M_PI)); //upperRight
    servo_write(i * 4 + 2, defaultAngle[i * 4 + 2] + (90 - acos((layerY[i] + 100) / 300.0) * 180 / M_PI)); //bottomLeft
    servo_write(i * 4 + 3, defaultAngle[i * 4 + 3] + (90 - acos((layerY[i] + 100) / 300.0) * 180 / M_PI)); //bottomRight
  }

}


