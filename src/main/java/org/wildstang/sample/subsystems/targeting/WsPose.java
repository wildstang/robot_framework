package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.DriveConstants;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.LimelightHelpers.PoseEstimate;

import java.util.Optional;

import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import java.util.Arrays;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsPose implements Subsystem {

    private WsAprilTagLL left;
    private WsAprilTagLL right;
    private WsAprilTagLL front;

    private WsAprilTagLL[] cameras; 

    // Object detection camera
    public WsGamePieceLL object = new WsGamePieceLL("limelight-object");

    private final double poseBufferSizeSec = 2;
    public final double visionSpeedThreshold = 3.0;
    
    public int currentID = 0;
    public SwerveDrive swerve;

    // WPI blue relative (m and CCW rad)
    public Pose2d odometryPose = new Pose2d();
    public Pose2d estimatedPose = new Pose2d();

    StructPublisher<Pose2d> odometryPosePublisher = NetworkTableInstance.getDefault().getStructTopic("odometryPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> estimatedPosePublisher = NetworkTableInstance.getDefault().getStructTopic("estimatedPose", Pose2d.struct).publish();
    StructPublisher<Pose2d> coralPosePublisher = NetworkTableInstance.getDefault().getStructTopic("coralPose", Pose2d.struct).publish();


    private final TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(poseBufferSizeSec);

    private SwerveModulePosition[] lastWheelPositions = {};
    private Rotation2d lastGyroAngle = new Rotation2d();

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        left = new WsAprilTagLL("limelight-left", swerve::getMegaTag2Yaw);
        right = new WsAprilTagLL("limelight-right", swerve::getMegaTag2Yaw);
        front = new WsAprilTagLL("limelight-object", swerve::getMegaTag2Yaw);
        cameras = new WsAprilTagLL[] {left, right, front};
    }

    @Override
    public void init() {
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        object.update();

        int bestIndex = -1;
        double bestStdDev = Double.MAX_VALUE;
        PoseEstimate bestEstimate = null;
        for (int i = 0; i < cameras.length; i++) {
            Optional<PoseEstimate> estimate = cameras[i].update();
            if (estimate.isPresent() && getStdDev(estimate) < bestStdDev) {
                bestIndex = i;
                bestEstimate = estimate.get();
                bestStdDev = getStdDev(estimate);
            }   
        }

        // If we found a valid estimate
        if (bestEstimate != null) {
            currentID = cameras[bestIndex].tid;
            addVisionObservation(bestEstimate, 1/bestStdDev);
        }

        if (getCoralPose().isPresent()) {
            coralPosePublisher.set(new Pose2d(getCoralPose().get(), new Rotation2d()));
        }

        odometryPosePublisher.set(odometryPose);
        estimatedPosePublisher.set(estimatedPose);
    }

    public double getStdDev(Optional<PoseEstimate> estimate) {
        return estimate.isPresent() && estimate.get().rawFiducials.length > 0 ? 
            Math.pow(Arrays.stream(estimate.get().rawFiducials).mapToDouble(fiducial -> fiducial.distToCamera).min().getAsDouble(),2) / estimate.get().tagCount 
            : Double.MAX_VALUE;
    }

    @Override
    public void resetState() {
        left.update();
        right.update();
    }

    /**
     * Reset estimated pose and odometry pose to pose
     * Clear pose buffer
    */
    public void resetPose(Pose2d initialPose) {
        estimatedPose = initialPose;
        odometryPose = initialPose;
        poseBuffer.clear();
    }

    public void addOdometryObservation(Pose2d newPose) {
        
        // Add pose to buffer at timestamp
        poseBuffer.addSample(Timer.getTimestamp(), newPose);

        estimatedPose = newPose;
    }

    private void addVisionObservation(PoseEstimate observation, double weight) {

        SmartDashboard.putNumber("auto weight", weight);
        Optional<Pose2d> sample = poseBuffer.getSample(observation.timestampSeconds);
        if (sample.isEmpty()) {
            // exit if not there
            return;
        }

        // sample --> odometryPose transform and backwards of that
        var sampleToOdometryTransform = new Transform2d(sample.get(), odometryPose);
        var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());

        // get old estimate by applying odometryToSample Transform
        Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

        // difference between estimate and vision pose
        Transform2d transform = new Transform2d(estimateAtTime, observation.pose);
        //transform = transform.times(Math.max(1, weight));

        // Recalculate current estimate by applying scaled transform to old estimate
        // then replaying odometry data
        estimatedPose = estimateAtTime.plus(transform).plus(sampleToOdometryTransform);
    }

    // YEAR SUBSYSTEM ACCESS METHODS


    /**
     * Can the object detection camera see a coral
     * @return true if a coral is present
     */
    public boolean coralInView(){
        return object.targetInView();
    }
    /**
     * Coral pose
     * 
     * @return field relative pose of the coral if the coral is present
     */
    public Optional<Translation2d> getCoralPose() {
        if (!object.targetInView()) return Optional.empty();

        Optional<Pose2d> sample = poseBuffer.getSample(object.timestamp);
        if (sample.isEmpty()) {
            // exit if not there
            return Optional.empty();
        }

        // current odometryPose --> sample transformation
        var odometryToSampleTransform = new Transform2d(odometryPose, sample.get());

        // get old estimate at timestamp by applying odometryToSample Transform
        Pose2d estimateAtTime = estimatedPose.plus(odometryToSampleTransform);

        // Assumes the angle of depression is gonna be negative
        double camToCoralDist = VisionConsts.camTransform.getZ() / -Math.tan(VisionConsts.camTransform.getRotation().getY() + Math.toRadians(object.ty));

        // Transform from camera to coral
        Transform2d camToCoralTransform = new Transform2d(Math.cos(Math.toRadians(-object.tx)) * camToCoralDist, Math.sin(Math.toRadians(-object.tx)) * camToCoralDist, Rotation2d.fromDegrees(-object.tx));

        Transform2d camTransform2d = new Transform2d(VisionConsts.camTransform.getX(), VisionConsts.camTransform.getY(), new Rotation2d(VisionConsts.camTransform.getRotation().getZ()));
        // Combines transformations to get coral pose in field coordinates
        return Optional.of(estimateAtTime.plus(camTransform2d).plus(camToCoralTransform).getTranslation());
    }

    // Set the front limlelight (same camera referenced by both front and object) to object or april tag pipeline
    public void setPipelineObject(boolean isObject) {
        if (isObject) {
            object.setPipeline(0);
        } else {
            front.setPipeline(1);
        }
    }

    public double turnToTarget(Translation2d target) {
        double offsetX = target.getX() - estimatedPose.getX();
        double offsetY = target.getY() - estimatedPose.getY();
        return (Math.toDegrees(Math.atan2(offsetY, offsetX)));
    }

    @Override
    public String getName() {
        return "Ws Pose";
    }
}