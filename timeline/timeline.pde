import controlP5.*;
import processing.serial.*;
import java.util.*;


int layerNum=3;
boolean usingSerial=false;

Serial arduino;
ControlP5 cp5;

int timeIndex=0;
boolean playing=false;

Timeline t[]=new Timeline[layerNum];
Timeline selectedTimeline=null;

int amount[]=new int[layerNum];

void setup() {
  size(1600, 1600);
  cp5 = new ControlP5(this);
  for (int i=0; i<t.length; i++) {
    t[i]=new Timeline(0, i*200, width-100, 200, 100);
    cp5.addSlider("layerAmount_"+i).setPosition(width-100, i*200).setSize(100, 180).setRange(-50, 50).setValue(amount[i]);
  }
  if (usingSerial) {
    arduino=new Serial(this, "COM8", 57600);
  }

  frameRate(60);
}

void draw() {
  if(playing&&frameCount%3==0){
    timeIndex++;
  }
  
  for (Timeline t1 : t) {
    t1.draw();
  }
  strokeWeight(3);
  stroke(0);
  for (int i=0; i<layerNum; i++) {
    line(0, i*200, width, i*200);
  }
  if (usingSerial&&frameCount%5==0) {
    for (int i=0; i<layerNum; i++) {
      arduino.write(t[i].getValue(amount[i]));
    }
    arduino.write(0);
  }
}

void mousePressed() {
  for (Timeline t1 : t) {
    if (t1.ifin()) {
      selectedTimeline=t1;
      break;
    }
  }
}

void mouseDragged() {
  if (selectedTimeline!=null) {
    selectedTimeline.drag();
  }
}

void keyReleased(){
  if(key==' '){
    if(playing){
      playing=false;
    }else{
      playing=true;
      timeIndex=0;
    }
  }
  if(key=='s'){
    String lines[]=new String[layerNum];
    for(int i=0;i<lines.length;i++){
      lines[i]="";
      for(int j=0;j<t[i].data.length;j++){
        if(j!=0){
          lines[i]+=",";
        }
        lines[i]+=t[i].data[j];
      }
    }
    saveStrings("data.text",lines);
  }
  if(key=='l'){
    String lines[]=loadStrings("data.text");
    for(int i=0;i<lines.length;i++){
      String data[]=lines[i].split(",");
      for(int j=0;j<data.length;j++){
        t[i].data[j]=int(data[j]);
      }
    }
  }
}

class Timeline {
  int data[];
  int width, height;
  int x, y;
  float scale=1;
  Timeline(int x, int y, int width, int height, int dataLength) {
    data=new int[dataLength];
    scale=1.0*(dataLength-1)/(width-1);
    this.width=width;
    this.height=height;
    this.x=x;
    this.y=y;
  }
  void draw() {
    strokeWeight(1);
    stroke(0);
    fill(255);
    rect(x, y, width, height);
    stroke(200);
    line(x, y+height/2, x+width, y+height/2);
    strokeWeight(2);
    noFill();
    stroke(0, 0, 100);
    beginShape();
    for (int i=0; i<data.length; i++) {
      vertex(x+i/scale, y+height/2-data[i]);
    }
    endShape();
    strokeWeight(1);
    stroke(255,0,0);
    {
      int x1=(int)(x+timeIndex%data.length/scale);
      line(x1,y,x1,y+height);
    }
  }
  void drag() {
    int x1=min(max(mouseX-x, 0), width-1);
    int y1=height/2-min(max(mouseY-y, 0), height);
    data[(int)(x1*scale)]=y1;
  }
  boolean ifin() {
    return x<=mouseX&&mouseX<x+width&&y<=mouseY&&mouseY<y+height;
  }
  int getValue(int amount) {
    return data[timeIndex%data.length]*amount/height+51;
  }
}

