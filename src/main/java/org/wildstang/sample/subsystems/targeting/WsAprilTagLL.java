package org.wildstang.sample.subsystems.targeting;

import java.util.Optional;
import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.PoseEstimate;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;

public class WsAprilTagLL implements LoggableInputs {


    public PoseEstimate alliance3D;
    public int tid;
    public boolean tv;
    public double tx;
    public double ty;
    public double ta;
    StructPublisher<Pose2d> posePublisher;

    // Method to get swerve drive gyro yaw
    DoubleSupplier yaw;

    // Name of Limelight
    public String CameraID;

    /*
     * Argument is String ID of the limelight networktable entry, aka what it's called
     */
    public WsAprilTagLL(String CameraID, DoubleSupplier yaw){
        posePublisher = NetworkTableInstance.getDefault().getStructTopic(CameraID + "/metatag2 alliance pose", Pose2d.struct).publish();
        this.yaw = yaw;
        this.CameraID = CameraID;
    }

    // Limelight NT botpose array values
    // 0 translation X
    // 1 translation Y
    // 2 translation Z
    // 3 rotation roll (degrees)
    // 4 rotation pitch
    // 5 rotation  yaw
    // 6 total latency (ms)
    // 7 tag count
    // 8 tag span
    // 9 average tag distance from camera
    // 10 average tag area (percentage of image)

    /**
     * Updates all values to the latest value
     */
    public Optional<PoseEstimate> update(){
        LimelightHelpers.SetRobotOrientation(CameraID, yaw.getAsDouble(), 0, 0, 0, 0, 0); 
        
        tid = (int) LimelightHelpers.getFiducialID(CameraID); // ID of the primary in view april tag
        tv = LimelightHelpers.getTV(CameraID); // 1 if valid target exists. 0 if no valid targets exist
        tx = LimelightHelpers.getTX(CameraID); // Horrizontal offset from crosshair to target
        ty = LimelightHelpers.getTY(CameraID); // Vertical offset from crosshair to target
        ta = LimelightHelpers.getTA(CameraID); // Target area

        double oldTimestamp = alliance3D != null ? alliance3D.timestampSeconds : Double.NaN;
        PoseEstimate blue3D = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(CameraID);
        PoseEstimate red3D = LimelightHelpers.getBotPoseEstimate_wpiRed_MegaTag2(CameraID);
        alliance3D = Core.isBlue() ? blue3D : red3D;
        boolean newEstimate = alliance3D != null ? (alliance3D.timestampSeconds != oldTimestamp) : false;
        

        Logger.processInputs("Vision/Camera/" + CameraID, this);
        if (newEstimate & tv) {
            posePublisher.set(alliance3D.pose);
            return Optional.of(alliance3D);
        } else {
            return Optional.empty();
        }
    }
    /*
     * returns true if a target is seen, false otherwise
     */
    public boolean targetInView(){
        return tv;
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