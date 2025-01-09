package org.wildstang.sample.subsystems.targeting;

import org.wildstang.framework.core.Core;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsLL {

    private final double mToIn = 39.3701;
    
    public NetworkTable limelight;

    //public LimelightHelpers.Results result;

    private double[] blue3D;
    private double[] red3D;
    public double[] target3D;
    public int tid;
    public double tv;
    public double tx;
    public double ty;
    public double tl;
    public double tc;
    public double ta;
    public String CameraID;
    public int numTargets;

    /*
     * Argument is String ID of the limelight networktable entry, aka what it's called
     */
    public WsLL(String CameraID){
        limelight = NetworkTableInstance.getDefault().getTable(CameraID);
        red3D = limelight.getEntry("botpose_wpired").getDoubleArray(new double[11]);
        //red3D = limelight.getEntry("botpose_orb_wpired").getDoubleArray(new double[11]);
        blue3D = limelight.getEntry("botpose_wpiblue").getDoubleArray(new double[11]);
        //blue3D = limelight.getEntry("botpose_orb_wpiblue").getDoublearray(new double[11]);
        target3D = Core.isBlue() ? blue3D : red3D;
        setToIn();
        tid = (int) limelight.getEntry("tid").getInteger(0);
        tv = limelight.getEntry("tv").getDouble(0);
        tx = limelight.getEntry("tx").getDouble(0);
        ty = limelight.getEntry("ty").getDouble(0);
        tl = limelight.getEntry("tl").getDouble(0);
        tc = limelight.getEntry("tc").getDouble(0);
        ta = limelight.getEntry("ta").getDouble(0);

        this.CameraID = CameraID;
        //result = LimelightHelpers.getLatestResults(CameraID).targetingResults;
    }

    /*
     * updates all values to the latest value
     */
    public void update(double yaw){
        LimelightHelpers.SetRobotOrientation(CameraID, yaw, 0, 0, 0, 0, 0); 

        //result = LimelightHelpers.getLatestResults(CameraID).targetingResults;
        tv = limelight.getEntry("tv").getDouble(0);
        tx = limelight.getEntry("tx").getDouble(0);
        ty = limelight.getEntry("ty").getDouble(0);
        if (tv > 0){
            blue3D = limelight.getEntry("botpose_wpiblue").getDoubleArray(new double[11]);
            //blue3D = limelight.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[11]);
            red3D = limelight.getEntry("botpose_wpired").getDoubleArray(new double[11]);
            //red3D = limelight.getEntry("botpose_orb_wpired").getDoubleArray(new double[11]);
            target3D = Core.isBlue() ? blue3D : red3D;
            setToIn();
            tid = (int) limelight.getEntry("tid").getInteger(0);
            //numTargets = result.targets_Fiducials.length;
        }
        updateDashboard();
    }
    /*
     * returns true if a target is seen, false otherwise
     */
    public boolean TargetInView(){
        return tv>0;
    }

    public void updateDashboard(){
        SmartDashboard.putBoolean(CameraID + " tv", TargetInView());
        SmartDashboard.putNumber(CameraID + " tid", tid);
        SmartDashboard.putNumber(CameraID + " numTargets", numTargets);
        SmartDashboard.putNumber(CameraID + "Vision x", target3D[0]);
        SmartDashboard.putNumber(CameraID + "Vision y", target3D[1]);
    }
    /*
     * returns total latency, capture latency + pipeline latency
     */
    public double getTotalLatency(){
        return tc + tl;
    }
    /*
     * Sets the pipeline (0-9) with argument
     */
    public void setPipeline(int pipeline){
        limelight.getEntry("pipeline").setNumber(pipeline);
    }

    /*
     * Sets what the LED lights do
     * 0 is pipeline default, 1 is off, 2 is blink, 3 is on
     */
    public void setLED(int ledState){
        limelight.getEntry("ledMode").setNumber(ledState);
    }

    /*
     * Sets camera mode, 0 for vision processing and 1 for just camera
     */
    public void setCam(int cameraMode){
        limelight.getEntry("camMode").setNumber(cameraMode);
    }

    /*
     * sets measurements to in from m
     */
    private void setToIn(){
        for (int i = 0; i < 7; i++){
            this.red3D[i] *= mToIn;
            this.blue3D[i] *= mToIn;
            this.target3D[i] *= mToIn;
        }
    }
    /**
     * returns distance to selected alliances' center of speaker for lookup table use
     */
    public double distanceToTarget(TargetCoordinate target){
        return Math.hypot(target3D[0] - target.getX(),
            target3D[1] - target.getY());
    }
    /*
     * gets control value for aligning robot to certain x value on the field
     */
    public double getAlignX(TargetCoordinate target){
        return -target3D[0]+target.getX();
    }
    /*
     * gets control value for aligning robot to certain y value on the field
     */
    public double getAlignY(TargetCoordinate target){
        return target3D[1] - target.getY();
    }

    /**
     * input of X and Y in frc field coordinates, returns controller bearing degrees (aka what to plug into rotLocked) for turnToTarget
     */
    public double getDirection(double x, double y) {
        double measurement = 90 + Math.toDegrees(Math.atan2(x,y));
        if (measurement < 0) {
            measurement = 360 + measurement;
        }
        else if (measurement >= 360) {
            measurement = measurement - 360;
        }
        return measurement;
    }
    /**
     * returns what to set rotLocked to
     */
    public double turnToTarget(TargetCoordinate target){
        return getDirection(target3D[0] - target.getX(),
            target3D[1] - target.getY());
    }

    /*
     * determine how many april tags a camera can see
     */
    public double getNumTags(){
        return target3D[7];
    }
    /*
     * determine the distance to the first april tag that a camera can see
     */
    public double getTagDist(){
        return target3D[9];
    }

}