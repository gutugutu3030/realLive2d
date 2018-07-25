import processing.serial.*;
import controlP5.*;
import java.util.*;

final byte COMMAND_EXPLORE=0x01;
final byte COMMAND_SAVEAMOUNT=0x02;

boolean usingSerial=true;

int layerAmount[]=new int[3];

Serial arduino;
ControlP5 cp5;


void setup() {
  size(1200, 1200);
  if (usingSerial) {
    arduino=new Serial(this, "COM8", 57600);
  }
  cp5=new ControlP5(this);
  for (int i=0; i<layerAmount.length; i++) {
    cp5.addSlider("layer_"+i)
      .setPosition(0, 80*i)
        .setRange(-50, 50)
          .setSize(width, 80)
            .setValue(layerAmount[i])
              .setId(i);
  }
  frameRate(60);
}



void draw() {
  background(0);
  if (frameCount%4==0&&usingSerial) {
    arduino.write(COMMAND_EXPLORE);
    for (int i=0; i<layerAmount.length; i++) {
      int x=(mouseX-width/2)*layerAmount[i]/width;
      arduino.write(x+51);
      arduino.write((height/2-mouseY)*layerAmount[i]/height+51);
    }    
    arduino.write(0);
  }
}


void controlEvent(ControlEvent theEvent) {
  if (theEvent.isController()) {
    // check if theEvent is coming from a boxsize controller
    if (theEvent.controller().getName().startsWith("layer_")) {
      // get the id of the controller and map the value
      // to an element inside the boxsize array.
      int id = theEvent.controller().getId();
      if (id>=0 && id<layerAmount.length) {
        layerAmount[id] = (int)theEvent.getValue();
      }
    }
  }
}

void keyReleased() {
  if (!usingSerial) {
    return;
  }
  arduino.write(COMMAND_SAVEAMOUNT);
  for (int i=0; i<layerAmount.length; i++) {
    arduino.write(layerAmount[i]+51);
  }
  arduino.write(0);
  println("write"+Arrays.toString(layerAmount));
}

