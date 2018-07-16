include<line.scad>


//bottom
servo3();
translate([63.4,0,0])
servo3();

translate([172.5,50,0])
rotate([0,0,90])
servo3();

translate([-11.9,50,0])
rotate([0,0,-90])
scale([-1,1,1])
servo3();

difference(){
    translate([-waku-11.9,-waku,0])
    cube([184.4+waku*2,120,15.8]);
    
    union(){
        servo3_rect();
        translate([63.4,0,0])
        servo3_rect();
        
        translate([130,0,0])
        servo3_rect(l=10);

        translate([172.5,50,0])
        rotate([0,0,90])
        servo3_rect();

        translate([172.5,110,0])
        rotate([0,0,90])
        servo3_rect(l=10);

        translate([-11.9,50,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servo3_rect();
        
        translate([-11.9,40-waku,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servo3_rect(l=10);
        
        translate([-11.9,110,0])
        rotate([0,0,-90])
        scale([-1,1,1])
        servo3_rect(l=10);
        
    }
}

//translate([0,110-waku,0])
//cube([1,1,300]);