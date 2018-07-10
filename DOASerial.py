import sys,os
sys.path.append('usb_4_mic_array')
from tuning import Tuning
import usb.core
import usb.util
import time
import serial

dev = usb.core.find(idVendor=0x2886, idProduct=0x0018)
ser = serial.Serial('COM3', 9600)
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
                x = direction/180.0 - 0.5
            else:
                x=(360-direction)/180.0 - 0.5
            if oldx!=x:
                oldx=x

                amount=[40,30,20,50]
                for i in range(4):
                    data = int(50+amount[i]*x)+1
                    ser.write(chr(data))
                    ser.write(y)
                ser.write(0)
            time.sleep(1)
        except KeyboardInterrupt:
            break