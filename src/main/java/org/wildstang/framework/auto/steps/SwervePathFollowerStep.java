package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import com.pathplanner.lib.path.PathPlannerTrajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.choreo.lib.*;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 39.3701;
    private SwerveDriveTemplate m_drive;
    private ChoreoTrajectory pathtraj;
    private boolean isBlue;

    private double xOffset, yOffset, prevVelocity, prevTime;
    private Pose2d localAutoPose, localRobotPose;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     * @param isBlue whether the robot is on the blue alliance
     */
    public SwervePathFollowerStep(ChoreoTrajectory pathData, SwerveDriveTemplate drive, boolean isBlue) {
        this.pathtraj = pathData;
        m_drive = drive;
        pathtraj = new ChoreoTrajectory();
        
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.setToAuto();
        timer.start();
        prevTime = 0.0;
        prevVelocity = 0.0;
    }

    @Override
    public void update() {
        if (timer.get() >= pathtraj.getTotalTime()) {
            m_drive.setAutoValues(0.0, -pathtraj.getFinalPose().getRotation().getDegrees(),0.0,0.0,0.0);
            SmartDashboard.putNumber("auto final time", timer.get());
            setFinished();
        } else {
            localRobotPose = m_drive.returnPose();
            localAutoPose = pathtraj.sample(timer.get()).getPose();//.poseMeters;
            yOffset = -(localRobotPose.getX() - localAutoPose.getX());
            if (isBlue){
                xOffset = localRobotPose.getY() - localAutoPose.getY();
            } else {
                xOffset = localRobotPose.getY() - (8.016 - localAutoPose.getY());
            }
            //update values the robot is tracking to
            m_drive.setAutoValues( getVelocity(),getHeading(), getAccel(), 2.0*xOffset,2.0*yOffset );
            prevVelocity = getVelocity();
            prevTime = timer.get();
            }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

    public double getVelocity(){
        return mToIn * Math.hypot(pathtraj.sample(timer.get()).velocityX, pathtraj.sample(timer.get()).velocityY);
    }
    public double getHeading(){
        if (isBlue) return ((-pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360; 
        else return ((pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360;
    }
    public double getAccel(){
        return (getVelocity() - prevVelocity) / (timer.get() - prevTime);
    }
}
