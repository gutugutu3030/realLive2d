include<line.scad>


//bottom
servoHolder(4);
translate([80.0,0,0])
servoHolder(4);

translate([185.8,50,0])
rotate([0,0,90])
servoHolder(4);

translate([-11.9,50,0])
rotate([0,0,-90])
scale([-1,1,1])
servoHolder(4);

difference(){
    translate([-waku-11.9,-waku,0])
    cube([194.4+waku*2,120+5,15.8]);
    
    union(){
        servoRect(4);
        translate([80.0,0,0])
        servoRect(4);
        
        translate([130,0,0])
        servoRect(l=10);

        translate([185.8,50,0])
        rotate([0,0,90])
        servoRect(4);

        translate([185.8,110,0])
        rotate([0,0,90])
        servoRect(l=10);

        translate([-11.9,50,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servoRect(4);
        
        translate([-11.9,40-waku,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servoRect(l=10);
        
        translate([-11.9,110,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servoRect(l=10);
        
    }
}

//translate([0,110-waku,0])
//cube([1,1,300]);