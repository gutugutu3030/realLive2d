//cube([100,100,15.8]);
servoW=12.6+0.4;
servoH=23.0+0.4;
    waku=4;

module servo(){
    cube([12.6+0.4,23.0+0.4,26.7]);
    translate([0,(23.0-32.4)/2,15.8])
    cube([12.6+0.4,32.4,2.4]);
    translate([(13-4.2)/2,23-8,-100])
    cube([4.2,13,18+100]);
}

module servos(num){
translate([servoW*4,0,0])
scale([-1,1,1])
    for(i = [0:num-1]){
        translate([i*13,0,(2.4+5)*i]){
            servo();
        }
    }
}

/**
* @deprecated
*/
module servo4_0(){
    servos(4);
}


module servoHolder(num){
translate([servoW*4,0,0])
scale([-1,1,1])
difference(){
union(){
    translate([-waku,-waku,0])
    cube([waku,waku*2+servoH,15.8]);
    translate([0,-waku,0]){
    for(i = [0:num-1]){
        translate([i*13,0,0]){
            cube([waku+servoW,waku*2+servoH,15.8+(2.4+5)*i]);
        }
    }
    }
}


union(){
    for(i = [0:num-1]){
        translate([i*13,0,(2.4+5)*i]){
            servo();
        }
    }
    
}
}
}

/**
*@deprecated
*/
module servo4(){
    servoHolder(4);
}

module servoRect(num, l=0){
    translate([-waku,-waku,0])
    cube([l==0?servoW*num+waku*2:l,servoH+waku+2,15.8]);
}

/**
* @deprecated
*/
module servo4_rect(l){
    servoRect(4, l);
}

module hikkake(l=servoW*4,d=10,fixPos=true,haba=1,thin=2,num=4){
    x=0;
    if(fixPos){
        x=-waku;
    }
    for(i=[1:1+num]){
        translate([0,-d,(2.4+5)*i+15.8+2.4-thin+1.5])
            cube([l,d,thin]);  
        translate([0,-d,(2.4+5)*i+15.8+2.4+haba+1.5])
            cube([l,d,thin]);  
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
