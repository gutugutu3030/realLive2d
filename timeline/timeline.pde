int layerNum=3;

Timeline t[]=new Timeline[layerNum];
Timeline selectedTimeline=null;

void setup() {
  size(1600, 1600);
  for (int i=0; i<t.length; i++) {
    t[i]=new Timeline(0, i*200, width, 200,100);
  }
}

void draw() {
  for (Timeline t1 : t) {
    t1.draw();
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

void mouseDragged(){
  if(selectedTimeline!=null){
    selectedTimeline.drag();
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
  }
  void drag() {
    int x1=min(max(mouseX-x, 0), width-1);
    int y1=height/2-min(max(mouseY-y, 0), height);
    data[(int)(x1*scale)]=y1;
  }
  boolean ifin() {
    return x<=mouseX&&mouseX<x+width&&y<=mouseY&&mouseY<y+height;
  }
}

