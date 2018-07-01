honeh=4;

difference(){
    union(){
        cylinder(r=6,h=6,$fn=180);
        translate([30-6,0,0])
        cylinder(r=6,h=6,$fn=180);
        translate([0,-6,0])
        cube([30-6,12,6]);
    }
    union(){
        translate([-10,-4,0])
        cube([30,8,honeh]);
        cylinder(r=1.5,h=100,$fn=50);
    }
}