void drawArm(){
  int waku=40;
  int r1=100,r2=servoH+waku*2-150;
  stroke(0);
  fill(0,255,0);
  rect(1300,-waku,r1,r2);
  rect(-waku-119,400-waku,r2,r1);
  
  rect(-waku-119,1100-waku,r2,r1);
  rect(1725+waku,1100-waku,-r2,r1);
}

void drawSeparator(float s){
    int waku=40;
  int r1=100,r2=servoH+waku*2-150;
  fill(0,0,255,10);
  //最大サイズ
  float mostLeft=s*(-119+r2-waku);
  float mostRight=s*(1725+waku-r2);
  float mostUp=s*(panelHeight+armL/2+servoH);
  float mostDown=s*(r2-waku);
  println("セパレータサイズ："+((mostRight-mostLeft)*0.1)+" "+((mostUp-mostDown)*0.1));
  //左アーム
  
  
  beginShape();
  vertex(mostLeft,mostUp);
  vertex(mostLeft,mostDown);
  vertex(mostRight,mostDown);
  vertex(mostRight,mostUp);
  endShape(CLOSE);
}
