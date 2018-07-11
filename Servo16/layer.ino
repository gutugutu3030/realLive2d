#include    <math.h>

void setLayerXY(float x, float y) {
  if (x < -0.5 || 0.5 < x)return;
  if (y < -0.5 || 0.5 < y)return;
  int amount[4] = {40, 30, 20, 50};

  for (int i = 0; i < 4; i++) {
    layerX[i] = 50 + (int)(x * amount[i]);
    layerY[i] = 50 + (int)(y * amount[i]);
  }
  writeLayer();
}

void writeLayer() {
  if (LAYER_LENGTH * 4 > 16) {
    return;
  }
  for (int i = 0; i < LAYER_LENGTH; i++) {
    servo_write(i * 4, defaultAngle[i * 4] + (acos((layerX[i] + 100) / 300.0) * 180 / M_PI)); //upperLeft
    servo_write(i * 4 + 1, defaultAngle[i * 4 + 1] + (90 - acos(1 - (layerX[i] + 100) / 300.0) * 180 / M_PI)); //upperRight
    servo_write(i * 4 + 2, defaultAngle[i * 4 + 2] + (90 - acos((layerY[i] + 100) / 300.0) * 180 / M_PI)); //bottomLeft
    servo_write(i * 4 + 3, defaultAngle[i * 4 + 3] + (90 - acos((layerY[i] + 100) / 300.0) * 180 / M_PI)); //bottomRight
  }

}


