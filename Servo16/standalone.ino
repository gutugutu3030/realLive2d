int cnt = 0;

void standalone() {
  //  for (int i = 0; i < 16; i++) {
  //    servo_write(i, defaultAngle[i]);
  //  }

  float angle = (cnt++) * 0.1;

  int amount[4]={40,30,20,50};
  
  for (int i = 0; i < 4; i++) {
    layerX[i] = 50+(int)(sin(angle)*amount[i]);
    layerY[i] = 50+(int)(cos(angle)*amount[i]);
  }

  writeLayer();
  delay(100);
}
