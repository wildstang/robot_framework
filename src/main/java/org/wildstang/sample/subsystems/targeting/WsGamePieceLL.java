package org.wildstang.sample.subsystems.targeting;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.PoseEstimate;

import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TimestampedDouble;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsGamePieceLL implements LoggableInputs {


    public PoseEstimate alliance3D;
    public int tid;
    private double cl;
    private double tl;
    public boolean tv;
    public double tx;
    public double ty;
    public double ta;
    DoubleEntry txEntry;

    // RoboRio relative timestmap of when the frame was taken
    public double timestamp;

    // Name of Limelight
    public String CameraID;

    /*
     * Argument is String ID of the limelight networktable entry, aka what it's called
     */
    public WsGamePieceLL(String CameraID){
        this.CameraID = CameraID;
        txEntry = NetworkTableInstance.getDefault().getDoubleTopic("/" + CameraID + "/tx").getEntry(0);
    }

    /**
     * Updates all values to the latest value
     */
    public void update(){  
        tid = (int) LimelightHelpers.getFiducialID(CameraID); // ID of the primary in view april tag
        cl = LimelightHelpers.getLatency_Capture(CameraID);
        tl = LimelightHelpers.getLatency_Pipeline(CameraID);
        tv = LimelightHelpers.getTV(CameraID); // 1 if valid target exists. 0 if no valid targets exist
        ty = LimelightHelpers.getTY(CameraID); // Vertical offset from crosshair to target
        ta = LimelightHelpers.getTA(CameraID); // Target area

        // Get timestamp based off of tx 
        TimestampedDouble tsValue = txEntry.getAtomic();
        tx = tsValue.value;

        // Time it was sent from LL - latency
        timestamp = (tsValue.timestamp / 1000000.0) - (getTotalLatency() / 1000.0);
        Logger.processInputs("Vision/Camera/" + CameraID, this);
    }
    /*
     * returns true if a target is seen, false otherwise
     */
    public boolean targetInView(){
        return tv;
    }

    private double getTotalLatency() {
        return tl + cl;
    }

    /*
     * Sets the pipeline (0-9) with argument
     */
    public void setPipeline(int pipeline){
        LimelightHelpers.setPipelineIndex(CameraID, pipeline);
    }

    public enum LEDState { DEFAULT, OFF, BLINK, ON}
    /*
     * Sets what the LED lights do
     */
    public void setLED(LEDState state){
        switch (state) {
            case DEFAULT:
                LimelightHelpers.setLEDMode_PipelineControl(CameraID);
            case OFF:
                LimelightHelpers.setLEDMode_ForceOff(CameraID);
            case BLINK:
                LimelightHelpers.setLEDMode_ForceBlink(CameraID);
            case ON:
                LimelightHelpers.setLEDMode_ForceOn(CameraID);
        }
    }

    @Override
    public void toLog(LogTable table) {
        //table.put("alliance3D", alliance3D);
        table.put("tid", tid);
        table.put("tv", tv);
        table.put("tx", tx);
        table.put("ty", ty);
        table.put("ta", ta);
    }

    @Override
    public void fromLog(LogTable table) {
        //alliance3D = table.get("alliance3D", alliance3D);
        tid = table.get("tid", tid);
        tv = table.get("tv", tv);
        tx = table.get("tx", tx);
        ty = table.get("ty", ty);
        ta = table.get("ta", ta);        
    }
}