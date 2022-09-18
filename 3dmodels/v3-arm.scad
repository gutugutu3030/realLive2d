include<line.scad>
//echo(servo*4+waku*2-0.4);
//translate([-10,0,0])
//cube([10-0.4,servoH+waku-0.4,(2.4+5)*4+15.8+2.4]);
//hikkake(l=10-0.4,d=2,thin=1,fixPos=false);
//servo();

arm();

module arm(){

waku1=3;

difference(){
    union(){
        cube([10-0.4,servoH+waku-0.4,15.8+0.4]);
        translate([0,15,0])
        {
            cube([10-0.4,servoH+waku-0.4-15,(2.4+5)*5+15.8+2.4+1+1.15+1.5]);
            hikkake(l=10-0.4,d=3,thin=1,fixPos=false, haba=1.2
);
        }
        translate([-waku1,-waku1,-1.5])
            cube([10-0.4+waku1*2,servoH+waku-0.4+waku1,1.5]);
        translate([-waku1,-waku1,15.8+0.4])
            cube([10-0.4+waku1*2,servoH+waku-0.4+waku1,1.5]);
        //servo4_0();
    }
    translate([(10-0.4-5)/2,-waku1,-50])
    cube([5,15+waku1,70]);
}
}