import sys,os
sys.path.append('usb_4_mic_array')
from tuning import Tuning
import usb.core
import usb.util
import time
import serial

dev = usb.core.find(idVendor=0x2886, idProduct=0x0018)
ser = serial.Serial('COM8', 57600)
#print dev
if dev:
    Mic_tuning = Tuning(dev)
    oldx=-1;
    while True:
        try:
            direction = Mic_tuning.direction
            print direction
            y = 50
            x = 0
            if direction <= 180:
                x = direction*100/180 +1
                print x
                if oldx!=x:
                    oldx=x  
                # ser.write(chr(x))
                # ser.write(chr(y))
                # ser.write(0)
                ser.write([x,y,0])
                print [(x-1)/100.0,(y-1)/100.0]
            time.sleep(0.1)
        except KeyboardInterrupt:
            break