//cube([100,100,15.8]);

servoW=12.6+0.4;
servoH=23.0+0.4;
    waku=4;

module servo(){
    cube([12.6+0.4,23.0+0.4,26.7]);
    translate([0,(23.0-32.4)/2,15.8])
    cube([12.6+0.4,32.4,2.4]);
    translate([(13-4.2)/2,23,-100])
    cube([4.2,5,18+100]);
}

module servo4_0(){
translate([servoW*4,0,0])
scale([-1,1,1])
    for(i = [0:3]){
        translate([i*13,0,(2.4+5)*i]){
            servo();
        }
    }
}

module servo4(){
translate([servoW*4,0,0])
scale([-1,1,1])
difference(){
union(){
    translate([-waku,-waku,0])
    cube([waku,waku*2+servoH,15.8]);
    translate([0,-waku,0]){
    for(i = [0:3]){
        translate([i*13,0,0]){
            cube([waku+servoW,waku*2+servoH,15.8+(2.4+5)*i]);
        }
    }
    }
}


union(){
    for(i = [0:3]){
        translate([i*13,0,(2.4+5)*i]){
            servo();
        }
    }
    
}
}
}

module servo4_rect(l=servoW*4+waku*2){
    translate([-waku,-waku,0])
    cube([l,servoH+waku+2,15.8]);

}


module hikkake(l=servoW*4,d=10){
    for(i=[0:4]){
        translate([-waku,waku+servoH,(2.4+5)*i+15.8+2.4])
        cube([l,d,2]);  
    }

}

//servo4_0();
//servo();
//hikkake();
//

//for(i=[0:4]){
//translate([-waku,waku+servoH,(2.4+5)*i+15.8+2.4])
//cube([servoW*4,10,2]);
//}
//translate([-waku,waku+servoH,(2.4+5)+15.8+2.4])
//cube([servoW*2+waku,10,2]);
//translate([-waku,waku+servoH,(2.4+5)*2+15.8+2.4])
//cube([servoW+waku,10,2]);
//
//translate([100,0,0])
//servo4_rect();
//
//translate([0,50,0])
//servo4_rect();
