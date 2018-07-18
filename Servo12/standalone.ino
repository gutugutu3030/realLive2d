int cnt = 0;

float fingerX = 0, fingerY = 0;
bool detectedFinger = false;
unsigned long lastFingerTime = 0;

float eyeX = 0, eyeY = 0;

void standalone() {
  //  for (int i = 0; i < 16; i++) {
  //    servo_write(i, defaultAngle[i]);
  //  }

  Skywriter.poll();

  float angle = (cnt++) * 0.1;

  int amount[LAYER_LENGTH] = {30, -22, 50};

  if (millis() - lastFingerTime<3000/* detectedFinger*/) {
    //まず指の検知をする
    detectedFinger = false;
    eyeX = fingerX;
    eyeY = fingerY;
  } else if (!getHotPixel(&eyeX, &eyeY)/*人の検知をする*/) {
    //次の動作

  }

  for (int i = 0; i < LAYER_LENGTH; i++) {
    layerX[i] = 50 + (int)(eyeX * amount[i]);
    layerY[i] = 50 + (int)(eyeY * amount[i]);
  }

  writeLayer();
  delay(50);
}



unsigned int oldx, oldy, oldz;
float alpha = 0.5;
float fingerAngle = 0;


void handle_xyz(unsigned int x, unsigned int y, unsigned int z) {
  if (oldx == x && oldy == y && oldz == z) {
    return;
  }
  oldx = x;
  oldy = y;
  oldz = z;

  // x z
  fingerX = fingerX * alpha + (x / 65535.0 - 0.5) * (1 - alpha);
  fingerY = fingerY * alpha + (z / 65535.0 - 0.5) * (1 - alpha);
  detectedFinger = true;
  lastFingerTime = millis();
}
