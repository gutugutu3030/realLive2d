import processing.serial.*;
import controlP5.*;
import java.util.*;

boolean usingSerial=true;

Serial arduino;
ControlP5 cp5;

int angle0[]=new int[16];

int index=0;

void setup() {
  size(1600, 1600);
  if (usingSerial) {
    arduino=new Serial(this, "COM8", 57600);
  }
  cp5=new ControlP5(this);
  for (int i=0; i<angle0.length; i++) {
    angle0[i]=30;
  }     

  for (int i=0; i<angle0.length; i++) {
    cp5.addSlider("servo_"+i)
      .setPosition(100, 100*i)
        .setRange(20, 40)
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

