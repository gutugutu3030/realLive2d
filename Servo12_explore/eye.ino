#define HOT_THRESHOLD 32

bool getHotPixel(float *x,float *y){
//  Serial.println("hot");
  float val=0;
  int index=-1;
  for(unsigned char i = 0; i < 64; i++){
    float tmp = grideye.getPixelTemperature(i);
      if(tmp > val){
        val=tmp;
        index = i;
      }
  }
  if(index==-1){
    return false;
  }
  if(HOT_THRESHOLD<=val){
    *y=-(index%8/8.0f-0.5f);
    *x=index/8/8.0f-0.5f;
//    Serial.print(*x);
//    Serial.print(",");
//    Serial.println(*y);
    return true;
  }
  return false;
}

