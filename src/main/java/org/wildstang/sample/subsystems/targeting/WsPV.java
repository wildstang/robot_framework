package org.wildstang.sample.subsystems.targeting;

import java.lang.annotation.Target;
import java.util.OptionalDouble;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsPV {

    public PhotonCamera camera;
    public String cameraID;
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    public PhotonTrackedTarget target;
    public PhotonPipelineResult result;
    private Transform3d robotToCamera = new Transform3d(new Translation3d(0.0, 0.0, 0.0), new Rotation3d(0,0,0));

    private VisionConsts VC = new VisionConsts();

    public int tid = 0;
    public double tx = 0;
    public double ty = 0;
    public boolean tv = false;
    public Transform3d aprilTag = new Transform3d();
    public Pose3d estimatedPose = new Pose3d();

    //whether the cam detects ATs or Notes
    public boolean isAT;

    public WsPV(String cameraID, boolean isAprilTag){
        this.cameraID = cameraID;
        camera = new PhotonCamera(cameraID);
        isAT = isAprilTag;
    }
    public WsPV(String cameraID){
        this(cameraID, false);
    }

    public void update(){
        result = camera.getLatestResult();
        SmartDashboard.putBoolean("hasTargets", result.hasTargets());
        tv = result.hasTargets();
        if(tv) {
            target = result.getBestTarget();
            tx = target.getYaw();
            ty = target.getPitch();

            //only used if it is an AT cam
            if(isAT){
                tid = target.getFiducialId();
                aprilTag = target.getBestCameraToTarget();
                estimatedPose = PhotonUtils.estimateFieldToRobotAprilTag(aprilTag,
                    aprilTagFieldLayout.getTagPose(target.getFiducialId()).get(), robotToCamera);
            }

        }
        updateDashboard();
    }

    public void updateDashboard(){
        SmartDashboard.putBoolean(cameraID + " tv", tv);
        SmartDashboard.putNumber(cameraID + " tid", tid);
        SmartDashboard.putNumber(cameraID + " Y", ty);
        SmartDashboard.putNumber(cameraID + " X", tx);
        SmartDashboard.putBoolean(cameraID + " isAT", isAT);
        SmartDashboard.putNumber(cameraID + "poseX", estimatedPose.getX()*VC.mToIn);
        SmartDashboard.putNumber(cameraID + "posey", estimatedPose.getY()*VC.mToIn);
    }

    /**
     * Toggles between Note and AT detection pipelines
     */
    public void togglePipeline() {
        isAT = !isAT;
        camera.setPipelineIndex(isAT ? VC.notePipelineIndex : VC.ATPipelineIndex);
    }

    /**
     * returns what to set rotLocked to
     */
    public double turnToTarget(boolean isBlue){
        return getDirection(estimatedPose.getX()*VC.mToIn - (VisionConsts.speaker.getX()),
            estimatedPose.getY()*VC.mToIn - (VisionConsts.speaker.getY()), isBlue);
    }

    /**
     * returns distance to selected alliances' center of speaker for lookup table use
     */
    public double distanceToTarget(boolean isBlue){
        return Math.hypot(estimatedPose.getX()*VC.mToIn - VisionConsts.speaker.getX(),
            estimatedPose.getY()*VC.mToIn - VisionConsts.speaker.getY());
    }

    /**
     * input of X and Y in frc field coordinates, returns controller bearing degrees (aka what to plug into rotLocked)
     */
    private double getDirection(double x, double y, boolean isBlue) {
        double measurement = Math.toDegrees(Math.atan2(x,y));
        if (isBlue) measurement += 90;
        else measurement -= 90;
        if (measurement < 0) {
            measurement = 360 + measurement;
        }
        else if (measurement >= 360) {
            measurement = measurement - 360;
        }
        return measurement;
    }
    public boolean TargetInView(){
        return tv;
    }
    public double getPoseX(){
        return estimatedPose.getX();
    }
    public double getPoseY(){
        return estimatedPose.getY();
    }

    public int getAprilTagID() {
        return tid;
    }

}
