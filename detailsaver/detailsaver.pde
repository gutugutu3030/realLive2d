import processing.serial.*;
import controlP5.*;
import java.util.*;

boolean usingSerial=true;
boolean avoidInterference =true;//v2土台との干渉対策　45度斜めでのキャリブレーション

Serial arduino;
ControlP5 cp5;

int angle0[]=new int[] {
  24, 35, 25, 20, 16, 24, 30, 23, 22, 23, 0, 23/*, 26, 13, 25, 31*/
};

int index=0;

void setup() {
  size(1600, 1600);
  if (usingSerial) {
    arduino=new Serial(this, "COM8", 57600);
  }
  cp5=new ControlP5(this);
  for (int i=0; i<angle0.length; i++) {
    int d=avoidInterference?45:0;
      angle0[i]+=d;

    cp5.addSlider("servo_"+i)
      .setPosition(100, 100*i)
        .setRange(20+d, 40+d)
          .setSize(width-100, 100)
            .setValue(angle0[i])
              .setId(i);
  }

  if (usingSerial) {
    new Thread(new Runnable() {
      public void run() {
        while (true) {
          try {
            Thread.sleep(100);
            for (int i=0; i<angle0.length; i++) {
              arduino.write(angle0[i]+1);
            }
            arduino.write(0);
          }
          catch(Exception e) {
          }
        }
      }
    }
    ).start();
  }

  textSize(30);
}

void draw() {
  background(100);
  println(Arrays.toString(angle0));
  fill(255);
  for (int i=0; i<angle0.length; i++) {
    text(i, 0, 50+100*i);
  }
}

void controlEvent(ControlEvent theEvent) {
  if (theEvent.isController()) {
    // check if theEvent is coming from a boxsize controller
    if (theEvent.controller().getName().startsWith("servo")) {
      // get the id of the controller and map the value
      // to an element inside the boxsize array.
      int id = theEvent.controller().getId();
      if (id>=0 && id<angle0.length) {
        angle0[id] = (int)theEvent.getValue();
      }
    }
  }
}

