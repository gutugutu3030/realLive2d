int cnt = 0;

float fingerX=0,fingerY=0;


void standalone() {
  //  for (int i = 0; i < 16; i++) {
  //    servo_write(i, defaultAngle[i]);
  //  }

  Skywriter.poll();

  float angle = (cnt++) * 0.1;

  int amount[4]={40,30,20,50};
  
  for (int i = 0; i < 4; i++) {
    layerX[i] = 50+(int)(fingerX*amount[i]);
    layerY[i] = 50+(int)(fingerY*amount[i]);
  }

  writeLayer();
  delay(50);
}



unsigned int oldx, oldy, oldz;
float alpha=0.5;
float fingerAngle=0;

unsigned long lastFingerTime = 0;


void handle_xyz(unsigned int x, unsigned int y, unsigned int z) {
  if (oldx == x && oldy == y && oldz == z) {
    return;
  }
  oldx=x;
  oldy=y;
  oldz=z;  

  // x z
  fingerX=fingerX*alpha+(x / 65535.0-0.5)*(1-alpha);
  fingerY=fingerY*alpha+(z / 65535.0-0.5)*(1-alpha);
  Serial.print(fingerX);
  Serial.print(" ");
  Serial.println(fingerY);
  lastFingerTime = millis();
}
