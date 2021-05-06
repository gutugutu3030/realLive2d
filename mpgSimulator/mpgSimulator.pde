import java.util.*;
import processing.serial.*;
import controlP5.*;
import javafx.util.*;
import java.util.stream.*;
import java.util.function.*;

boolean useSerial=false;//シリアルでservo16におくるかどうか
boolean autoMove=true;

ControlP5 cp5;
Serial arduino;

ArrayList<Servo> servos=new ArrayList<Servo>();

float panelY=0;
float panelX=0;
float panelRotate = 0;
PVector panelSize = new PVector(250, 250);

static final int TATE=0;
static final int YOKO1=1;
static final int YOKO2=2;
static final float servoW=11.8;
static final float servoH=22.2;
float armL=110;

int servoYDistance = 230;
int servoXsY = 50;

float limitFunctionA = -1.6;
float limitFunctionB = 40;
float offsetOfServo = 10;

void settings() {
  size(1800, 1800);
}

Servo servoX, servoY1, servoY2;

void setup() {
  cp5=new ControlP5(this);
  cp5.addSlider("servoYDistance")
    .setPosition(0, 0)
    .setRange(200, 230)
    .setSize(width, 30);
  cp5.addSlider("armL")
    .setPosition(0, 30)
    .setRange(90, 120)
    .setSize(width, 30);
  cp5.addSlider("servoXsY")
    .setPosition(0, 60)
    .setRange(-100, 100)
    .setSize(width, 30);
  cp5.addSlider("offsetOfServo")
    .setPosition(0, 90)
    .setRange(0, 50)
    .setSize(width, 30);
  cp5.addSlider("limitFunctionA")
    .setPosition(0, 130)
    .setRange(-20,-0.5)
    .setSize(width, 30);
  cp5.addSlider("limitFunctionB")
    .setPosition(0, 160)
    .setRange(20,100)
    .setSize(width, 30);
  
  cp5.addSlider("panelX")
    .setPosition(0, height-90)
    .setRange(-30, 30)
    .setSize(width, 30);
  cp5.addSlider("panelY")
    .setPosition(0, height-60)
    .setRange(-30, 30)
    .setSize(width, 30);
  cp5.addSlider("panelRotate")
    .setPosition(0, height-30)
    .setRange(-15, 15)
    .setSize(width, 30);

  if (useSerial) {
    arduino=new Serial(this, "COM3", 57600);
  }

  servoY1 = new Servo(servos, 0, 0, 0, YOKO2);
  servoY2 = new Servo(servos, 0, 0, 0, YOKO1);
  servoX = new Servo(servos, 0, 0, -HALF_PI, TATE);
  textAlign(CENTER,CENTER);
}

void draw() {
  pushMatrix();
  background(255);
  float scale = 3.5;
  scale(scale, -scale);
  PVector size = new PVector(width/scale, height/scale);
  translate(size.x/2, -size.y/2);
  pushMatrix();
  fill(255, 50);
  stroke(0, 50);
  rect(-panelSize.x/2, -panelSize.y/2, panelSize.x, panelSize.y);

  float servoYRail = -panelSize.y/2 + 10; //高さレールの座標
  float servoY = servoYRail - armL/2 - offsetOfServo; //高さサーボの回転軸高さ
  float servoXRail = servoYRail;//横レールの座標

  //計算式をここに書く
  //サーボ位置の指定
  //高さ
  servoY1.setXY(-servoYDistance/2, servoY);
  servoY2.setXY(servoYDistance/2, servoY);
  //横
  servoX.setXY(servoXRail - armL/2 - offsetOfServo, servoXsY);

  setPanelPosition();
  
  //サーボ角度の指定
  setAngle();

  for (Servo servo : servos) {
    servo.draw();
  }
  noFill();
  translate(panelX, panelY);
  rotate(radians(panelRotate));
  rect(-panelSize.x/2, -panelSize.y/2, panelSize.x, panelSize.y);//板
  line(-panelSize.x/2 + 10, servoYRail, panelSize.x/2 - 10, servoYRail);
  line(servoXRail, -panelSize.y/2 + 10, servoXRail, panelSize.y/2 - 10);
  popMatrix();
  drawDistance();
  popMatrix();
}

void setPanelPosition(){
  if(!autoMove){
    return;
  }
  float maxMoveR = limitFunctionA * abs(panelRotate) + limitFunctionB;
  panelX = maxMoveR * sin(frameCount * 0.1);
  panelY = maxMoveR * cos(frameCount * 0.1);
}

void setAngle() {
  float servoYRail = -panelSize.y/2 + 10; //高さレールの座標
  float servoY = servoYRail - armL/2; //高さサーボの回転軸高さ
  float servoXRail = servoYRail;//横レールの座標
  float mizoDY = panelSize.y / 2 - 10;//中心から下ミゾまでの長さ
  float mizoDX = panelSize.x / 2 - 10;//中心から横ミゾまでの長さ

  if (abs(panelRotate)<0.0001) {
    //回転なし
    servoY1.setAngle(asin((panelY + offsetOfServo)/armL+0.5));
    servoY2.setAngle(PI-asin((panelY + offsetOfServo)/armL+0.5));
    servoX.setAngle(asin((panelX + offsetOfServo)/armL+0.5));
    return;
  }
  //回転あり
  boolean occurredError = false;
  {
    //下のミゾの直線の式 y = mx + n;
    final float m = tan(radians(panelRotate)), n = - mizoDY / cos(radians(panelRotate))-m*panelX+panelY;
    occurredError = Stream.of(servoY1, servoY2).map(s->
      new Pair<Servo, List<PVector>>(s, getIntersection(s, m, n)))
      .filter(Predicate.not(p->p.getValue().isEmpty()))//頂点の解がある
      .map(p->new Pair<Servo,Optional<Float>>(p.getKey(),getIntersectionPointAngle(p.getKey(),p.getValue())))
      .peek(p->p.getValue().ifPresent(f->p.getKey().setAngle(f)))
      .filter(p->p.getValue().isPresent()).count()!=2;
  }
  {
    //横のミゾの直線の式 y = mx + n;
    final float m = tan(radians(panelRotate)+HALF_PI), n = - mizoDX / sin(radians(panelRotate))-m*panelX + panelY;
    occurredError = occurredError || Stream.of(servoX).map(s->
      new Pair<Servo, List<PVector>>(s, getIntersection(s, m, n)))
      .filter(Predicate.not(p->p.getValue().isEmpty()))//頂点の解がある
      .map(p->new Pair<Servo,Optional<Float>>(p.getKey(),getIntersectionPointAngle(p.getKey(),p.getValue())))
      .peek(p->p.getValue().ifPresent(f->p.getKey().setAngle(f+ HALF_PI)))
      .filter(p->p.getValue().isPresent()).count()!=1;
  }
  println(occurredError);
  if(occurredError){
    fill(0);
    textMirror("error!", 0,0);
  }
}

float getAngle(PVector center, PVector tip){
  return atan2(tip.y - center.y, tip.x - center.x);
}

Optional<Float> getIntersectionPointAngle(Servo s, List<PVector> intersections){
  return intersections.stream().sorted(Comparator.comparing(PVector::mag)).findFirst()
    //.filter(p->s.getRotateCenter().dist(p)<=armL)
    .map(p->getAngle(s.getRotateCenter(), p));
}

List<PVector> getIntersection(Servo s, float m, float n) {
  return Optional.of(s.getRotateCenter()).map(p-> getIntersection(armL, p.x, p.y, m, n)).orElseThrow();
}

void drawDistance(){
  stroke(0,255,0);
  //draw y tip
  PVector y1tip = servoY1.getTip();
  PVector y2tip = servoY2.getTip();
  line(y1tip.x,y1tip.y,y2tip.x,y2tip.y);
  fill(0);
  
  Optional.of(PVector.add(y1tip,y2tip).div(2)).ifPresent(m->textMirror(""+y1tip.dist(y2tip),m.x,m.y));
  
  //draw servo position
  Stream.of(servoX,servoY1,servoY2).forEach(s->textMirror("("+s.x+","+s.y+")\n"+s.getRotateCenter(),s.x,s.y));
}

void textMirror(String text, float x,float y){
  pushMatrix();
  translate(x,y);
  scale(1,-1);
  text(text,0,0);
  popMatrix();
}





/**
 *円と直線の交点を求めます
 */
List<PVector> getIntersection(float cr, float cx, float cy, float m, float n) {
  double a = 1 + Math.pow(m, 2);
  double b = -2 * cx + 2 * m * (n - cy);
  double c = Math.pow(cx, 2) + Math.pow((n - cy), 2) - Math.pow(cr, 2);
  double D = Math.pow(b, 2) - 4 * a * c;
  if (D < 0) {
    return List.of();
  }
  double x1 = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
  if (D<0.00001) {
    return List.of(new PVector((float)x1, (float)(m*x1+n)));
  }
  double x2 = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
  return List.of(
    new PVector((float)x1, (float)(m*x1+n)),
    new PVector((float)x2, (float)(m*x2+n))
    );
}



class Servo {
  float x, y;
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
  void setXY(float x, float y) {
    this.x = x;
    this.y = y;
    Optional.of(getRotateCenterDiff()).ifPresent(p->translateDiff(-p.x, -p.y));
  }
  void translateDiff(float x, float y) {
    this.x += x;
    this.y += y;
  }
  PVector getRotateCenter() {
    return getRotateCenterDiff().add(new PVector(x, y));
  }
  PVector getRotateCenterDiff() {
    switch(dir) {
    case TATE:
      return new PVector(servoW/2, servoW/2);
    case YOKO1:
      return new PVector(servoW/2, servoW/2);
    case YOKO2:
    default:
      return new PVector(servoH-servoW/2, servoW/2);
    }
  }
  PVector getTip(){
    float x1=0, y1=0;
    switch(dir) {
    case TATE:
      x1=x+servoW/2;
      y1=y+servoW/2;
      break;
    case YOKO1:
      x1=x+servoW/2;
      y1=y+servoW/2;
      break;
    case YOKO2:
      x1=x+servoH-servoW/2;
      y1=y+servoW/2;
      break;
    }
    return new PVector(x1+cos(-angle-defaultAngle)*armL, y1-sin(-angle-defaultAngle)*armL);
  }
  void draw() {
    float w=0, h=0, x1=0, y1=0;
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
    noFill();
    rect(x-4, y-4, w+8, h+8);
    fill(100, 100, 255);
    rect(x, y, w, h);
    strokeWeight(0.5);
    line(x1, y1, getTip().x,getTip().y);
    strokeWeight(0.5);
    stroke(255, 0, 0);
    line(x1, y1, x1+cos(-defaultAngle)*armL, y1-sin(-defaultAngle)*armL);
  }
}
