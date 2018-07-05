import java.util.*;
import processing.serial.*;
import controlP5.*;

boolean useSerial=false;
boolean visibleMeasure=true;
boolean allControll=true;

ControlP5 cp5;
Serial arduino;


//Servo upperLeft, upperRight;
//Servo bottomLeft, bottomRight;
ArrayList<Servo> servos=new ArrayList<Servo>();

int panelY=0;
int panelX=0;
int panelpos[][]=new int[4][2];
PImage img[]=new PImage[4];
PImage backimg=null;
int upperHeight=500, bottomWidth=280;
int upperWidth=1400;
int panelHeight=1218;

static final int TATE=0;
static final int YOKO1=1;
static final int YOKO2=2;
static final int servoW=118;
static final int servoH=222;
static final int armL=300;

Layer layer[];

void setup() {
  size(2000, 1600);
  //  upperLeft=new Servo(servos, 100, 200, -HALF_PI, YOKO2);
  //  upperRight=new Servo(servos, width-100-servoH, 200, PI, YOKO1);
  //  bottomLeft=new Servo(servos, 500, 1200, 0, TATE);
  //  bottomRight=new Servo(servos, width-500-servoW, 1200, HALF_PI, TATE);
  layer=new Layer[4];
  for (int i=0; i<layer.length; i++) {
    layer[i]=new Layer();
    img[i]=loadImage(dataPath(i+".png"));
    panelpos[i][0]=50;
    panelpos[i][1]=50;
  }
  backimg=loadImage(dataPath("-1.png"));

  cp5=new ControlP5(this);
  if (visibleMeasure) {
    cp5.addSlider("upperHeight")
      .setPosition(0, 0)
        .setRange(0, height-100)
          .setSize(width, 50);
    cp5.addSlider("upperWidth")
      .setPosition(0, 50)
        .setRange(0, width)
          .setSize(width, 50);
    cp5.addSlider("bottomWidth")
      .setPosition(0, 100)
        .setRange(servoW, width)
          .setSize(width, 50);
  }


  //  cp5.addSlider("panelX")
  //    .setPosition(0, height-100)
  //      .setRange(0, 100)
  //        .setSize(width, 50);
  //  cp5.addSlider("panelY")
  //    .setPosition(0, height-50)
  //      .setRange(0, 100)
  //        .setSize(width, 50);

  if (!allControll) {
    for (int i=0; i<panelpos.length*2; i++) {
      cp5.addSlider("panel"+(i%2==0?"X_":"Y_")+(i/2))
        .setPosition(100+width/2*(i%2), height-400+i/2*100)
          .setRange(0, 100)
            .setSize(width/2-100, 100)
              .setValue(panelpos[i/2][i%2])
                .setId(i);
    }
  } else {
    cp5.addSlider("panelX")
      .setPosition(0, height-100)
        .setRange(0, 100)
          .setSize(width, 50);
    cp5.addSlider("panelY")
      .setPosition(0, height-50)
        .setRange(0, 100)
          .setSize(width, 50);
  }

  if (useSerial) {
    arduino=new Serial(this, "COM3", 57600);
  }
}

void draw() {
  background(255);
  //  upperLeft.setAngle(HALF_PI-acos(panelX/armL));
  //  upperRight.setAngle(acos(1-panelX/armL));
  //  bottomLeft.setAngle(HALF_PI-acos(panelY/armL));
  //  bottomRight.setAngle(acos(panelY/armL));
  //
  //  upperLeft.y=upperHeight;
  //  upperRight.y=upperHeight;
  //  bottomLeft.x=width/2-bottomWidth/2-servoH/2;
  //  bottomRight.x=width/2+bottomWidth/2-servoH/2;

  update();

  for (int i=0; i<layer.length; i++) {
    layer[i].setPos(upperHeight+i*servoW, upperWidth, bottomWidth+i*servoW*2);
      if(allControll){
        layer[i].update(panelX, panelY);
      }else{
        layer[i].update(panelpos[i][0], panelpos[i][1]);
      }

    
  }
  if (useSerial) {
    for (int i=0; i<layer.length; i++) {
      layer[i].sendSerial();
    }
    arduino.write(0);
  }


  for (Servo servo : servos) {
    servo.draw();
  }
  fill(255, 50);
  stroke(0);
  float px=150+width/2-upperWidth/2-servoW/2;//=panelX+100+servoH-servoW/2;
  float py=1200+servoW/2-150-panelHeight;//1200+servoW/2-panelY-800;
  float pw=upperWidth+servoW-armL;
  float ph=panelHeight;
  rect(px, py, pw, ph);
  if (backimg!=null) {
    image(backimg, px, py, pw, ph);
  }
  for (int i=layer.length-1; i>=0; i--) {
    PVector pos[]=layer[i].getPanelParams();
    println(pos[1].x+" "+ pos[1].y);
    rect(pos[0].x, pos[0].y, pos[1].x, pos[1].y);
    if (img[i]!=null) {
      image(img[i], pos[0].x, pos[0].y, pos[1].x, pos[1].y);
    }
  }

  if (visibleMeasure) {
    fill(0);
    textSize(30);
    //    text("("+px+","+py+","+pw+","+ph+")", px, py+30);
    //    line(px+pw/2, 0, px+pw/2, height);

    stroke(255, 0, 0);
    line(width/2, 0, width/2, height);
    float r=(width-upperWidth)/2-servoH;
    line(r,height/2-50,width-r-servoH,height/2-50);
    text(""+(width-r*2-servoH), r, height/2-50);
    line(r, height/2, width-r, height/2);
    text(""+(width-r*2), width/2, height/2);
    line(r, height*2/3, width/2-bottomWidth/2-servoW/2-armL-servoW*3+50, height*2/3);
    text(""+(width/2-bottomWidth/2-servoW/2-armL-servoW*3+50-r), 
    width/2-bottomWidth/2-servoW/2-armL-servoW*3+50, height*2/3);
    line(width/2+bottomWidth/2-servoW/2-armL+servoW*4+50, height*2/3, width-r, height*2/3);
    text(""+(width-r - (width/2+bottomWidth/2-servoW/2-armL+servoW*4+50)), 
    width/2+bottomWidth/2-servoW/2-armL+servoW*4+50, height*2/3);
    line(width/2-bottomWidth/2-servoW*7/2-armL+50,height-230,width/2+bottomWidth/2-servoW/2-armL+50,height-230);
    text(""+((width/2+bottomWidth/2-servoW/2-armL+50)-(width/2-bottomWidth/2-servoW*7/2-armL+50)),width/2+bottomWidth/2-servoW/2-armL+50,height-230);
    //  line(mouseX,0,mouseX,height);
  }
}


void controlEvent(ControlEvent theEvent) {
  if (theEvent.isController()) {
    // check if theEvent is coming from a boxsize controller
    if (theEvent.controller().getName().startsWith("panel")) {
      // get the id of the controller and map the value
      // to an element inside the boxsize array.
      int id = theEvent.controller().getId();
      if (id>=0 && id<panelpos.length*2) {
        panelpos[id/2][id%2] = (int)theEvent.getValue();
      }
    }
  }
}

class Servo {
  int x, y;
  float angle, defaultAngle;
  int dir=0;
  Servo(List<Servo> list, int x, int y, float defaultAngle, int dir) {
    this.x=x;
    this.y=y;
    this.defaultAngle=defaultAngle;
    this.dir=dir;
    list.add(this);
  }
  void setAngle(float angle) {
    this.angle=angle;
  }
  void setEquipDirection(int dir) {
    this.dir=dir;
  }
  void draw() {
    int w=0, h=0, x1=0, y1=0;
    switch(dir) {
    case TATE:
      w=servoW;
      h=servoH;
      x1=x+servoW/2;
      y1=y+servoW/2;
      break;    
    case YOKO1:
      w=servoH;
      h=servoW;
      x1=x+servoW/2;
      y1=y+servoW/2;
      break;    
    case YOKO2:
      w=servoH;
      h=servoW;
      x1=x+w-servoW/2;
      y1=y+servoW/2;
      break;
    }
    stroke(0);
    fill(100, 100, 255);
    rect(x, y, w, h);
    strokeWeight(10);
    line(x1, y1, x1+cos(-angle-defaultAngle)*armL, y1+sin(-angle-defaultAngle)*armL);
    strokeWeight(1);
    stroke(255, 0, 0);
    line(x1, y1, x1+cos(-defaultAngle)*armL, y1+sin(-defaultAngle)*armL);
  }
}

