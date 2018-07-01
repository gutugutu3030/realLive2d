#include    <math.h>

void writeLayer(){
  if(LAYER_LENGTH*4>16){
    return;
  }
  for(int i=0;i<LAYER_LENGTH;i++){
    servo_write(i*4, defaultAngle[i*4]+(acos((layerX[i]+100)/300.0) * 180 / M_PI));//upperLeft
    servo_write(i*4+1, defaultAngle[i*4+1]+(90-acos(1-(layerX[i]+100)/300.0) * 180 / M_PI));//upperRight
    servo_write(i*4+2, defaultAngle[i*4+2]+(90-acos((layerY[i]+100)/300.0) * 180 / M_PI));//bottomLeft
    servo_write(i*4+3, defaultAngle[i*4+3]+(90-acos((layerY[i]+100)/300.0) * 180 / M_PI));//bottomRight
  }
  
}

