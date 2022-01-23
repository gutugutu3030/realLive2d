honeH=3;
honeAllH=4;

module attachedHone(){
    linear_extrude(height = honeH)
        polygon([[0,7.4/2],[17.5,4/2],[17.5,-4/2],[0,-7.4/2]]);
}

difference(){
    union(){
        cylinder(r=6,h=honeAllH,$fn=180);
        linear_extrude(height = honeAllH)
        polygon([[0,6],[30,0],[0,-6]]);
    }
    union(){
        attachedHone();
        scale([-1,1,1])
        attachedHone();
        cylinder(r=1.5,h=100,$fn=50);
    }
}