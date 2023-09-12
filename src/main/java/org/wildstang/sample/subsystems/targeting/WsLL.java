package org.wildstang.sample.subsystems.targeting;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsLL {

    private final double mToIn = 39.3701;
    
    public NetworkTable limelight;

    public LimelightHelpers.Results result;

    public double[] blue3D;
    public double[] red3D;
    public double tid;
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
        red3D = limelight.getEntry("botpose_wpired").getDoubleArray(new double[7]);
        blue3D = limelight.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        setToIn();
        tid = limelight.getEntry("tid").getDouble(0);
        tv = limelight.getEntry("tv").getDouble(0);
        tx = limelight.getEntry("tx").getDouble(0);
        ty = limelight.getEntry("ty").getDouble(0);
        tl = limelight.getEntry("tl").getDouble(0);
        tc = limelight.getEntry("tc").getDouble(0);
        ta = limelight.getEntry("ta").getDouble(0);

        this.CameraID = CameraID;
        result = LimelightHelpers.getLatestResults(CameraID).targetingResults;
    }

    /*
     * updates all values to the latest value
     */
    public void update(){
        result = LimelightHelpers.getLatestResults(CameraID).targetingResults;
        tv = limelight.getEntry("tv").getDouble(0);
        if (tv > 0){
            blue3D = limelight.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
            red3D = limelight.getEntry("botpose_wpired").getDoubleArray(new double[7]);
            setToIn();
            tid = limelight.getEntry("tid").getDouble(0);
            numTargets = result.targets_Fiducials.length;
        }
        updateDashboard();
    }
    /*
     * returns true if a target is seen, false otherwise
     */
    public boolean TargetInView(){
        return tv>0;
    }

    /*
     * returns true if seeing a blue apriltag, false if a red one
     */
    public boolean isSeeingBlue(){
        return tid > 4.5;
    }

    public void updateDashboard(){
        SmartDashboard.putBoolean(CameraID + " tv", TargetInView());
        SmartDashboard.putNumber(CameraID + " tid", tid);
        SmartDashboard.putNumber(CameraID + " numTargets", numTargets);
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

    private void setToIn(){
        for (int i = 0; i < 7; i++){
            this.red3D[i] *= mToIn;
            this.blue3D[i] *= mToIn;
        }
    }
}