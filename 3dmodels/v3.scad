include<line.scad>

difference(){
union(){

    //bottom
    servoHolder(4);
    translate([80.0,0,0])
    servoHolder(4);

    translate([185.8,50,0])
    rotate([0,0,90])
    servoHolder(4);

    translate([-8.6,50,0])
    rotate([0,0,-90])
    scale([-1,1,1])
    servoHolder(4);

    difference(){
        translate([-waku-8.6,-waku,0])
        cube([194.4+waku*2,120+5,15.8]);
        
        union(){
            servoRect(4);
            translate([80.0,0,0])
            servoRect(4);
            
            translate([140,0,0])
            servo4_rect(10);

            translate([185.8 ,50,0])
            rotate([0,0,90])
            servoRect(4);

            translate([185.8 ,110,0])
            rotate([0,0,90])
            servoRect(l=10);

            translate([-8.6,50,0])
            rotate([0,0,-90])
            scale([-1,1,1])
            servoRect(4);
            
            translate([-8.6,40-waku,0])
            rotate([0,0,-90])
            scale([-1,1,1])
            servoRect(l=10);
            
            translate([-8.6,110,0])
            rotate([0,0,-90])
            scale([-1,1,1])
            servoRect(l=10);
            
        }
    }
}
translate([-8.6+servoH+waku+10,servoH+waku+10,0])
cube([8.9+185.8 - servoH*2 - waku*2-20,100,100]);
}