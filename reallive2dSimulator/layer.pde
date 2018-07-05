class Layer {
  Servo upperLeft, upperRight;
  Servo bottomLeft, bottomRight;
  int panelX, panelY;
  Layer() {
    upperLeft=new Servo(servos, 100, 200, 0, YOKO2);
    upperRight=new Servo(servos, width-100-servoH, 200, HALF_PI, YOKO1);
    bottomLeft=new Servo(servos, 500, 1200, 0, TATE);
    bottomRight=new Servo(servos, width-500-servoW, 1200, 0, TATE);
  }
  void setPos(int upperHeight, int upperWidth, int bottomWidth) {
    upperLeft.x=width/2-upperWidth/2-servoH;
    upperRight.x=width/2+upperWidth/2;
    upperLeft.y=bottomLeft.y+servoH-upperHeight-servoW;
    upperRight.y=bottomLeft.y+servoH-upperHeight-servoW;
    bottomLeft.x=width/2-bottomWidth/2-servoW/2-armL+50;
    bottomRight.x=width/2+bottomWidth/2-servoW/2-armL+50;
  }
  void update(int panelX, int panelY) {
    upperLeft.setAngle(acos(1.0*(panelX+100.0)/armL));
    upperRight.setAngle(HALF_PI-acos(1-(panelX+100.0)/armL));
    bottomLeft.setAngle(HALF_PI-acos((panelY+100.0)/armL));
    bottomRight.setAngle(HALF_PI-acos((panelY+100.0)/armL));
    this.panelX=panelX;
    this.panelY=panelY;
  }
  void sendSerial() {
    arduino.write(panelX);
    arduino.write(panelY);
  }
  PVector[] getPanelParams() {
    float px=panelX+width/2-upperWidth/2-servoW/2+100;//=panelX+100+servoH-servoW/2;
    float py=1200+servoW/2-panelY-panelHeight-100;//1200+servoW/2-panelY-800;
    float pw=upperWidth+servoW-armL;
    float ph=panelHeight;
    return new PVector[] {
      new PVector(px, py), new PVector(pw, ph)
    };
  }
}

