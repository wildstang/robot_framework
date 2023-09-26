package org.wildstang.sample.subsystems.targeting; 

public class LimeConsts {

    public double VERTICAL_APRILTAG_DISTANCE = 73.0; 

    public double HORIZONTAL_APRILTAG_DISTANCE_LEFT = 33.0; 

    public double HORIZONTAL_APRILTAG_DISTANCE_RIGHT = 33.0;

    public double HORIZONTAL_LIMELIGHT_MOUNT = 6.4;

    public double VERT_AUTOAIM_P = 0.01;

    public double HORI_AUTOAIM_P = 0.01;

    public double OFFSET_HORIZONTAL = 10;

    public double OFFSET_VERTICAL = -5;

    public double[] CUBES = {6.938,  5.268,  3.592,  4.425,  2.75,   1.072}; //meters
    public double[] CONES = {7.503, 6.385, 5.827, 4.709, 4.150, 3.033, 4.989, 3.871, 3.307, 2.189, 1.631, 0.513};//meters

    public double STATION_VERTICAL = 9.0 + 42.19;//inches

    public double RED_STATION_X = 1.266*39.3701;//inches
    public double BLUE_STATION_X = 6.75*39.3701;//inches
    public double STATION_HORIZONTAL = 24.0;//inches
    public double STATION_OFFSETS = 20;
    
    public final boolean CONE = true;
    public final boolean CUBE = false;
}