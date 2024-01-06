package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.choreo.lib.*;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 39.3701;
    private SwerveDriveTemplate m_drive;
    private PathPlannerTrajectory pathData;
    private ChoreoTrajectory pathtraj;
    private boolean isBlue;

    private double xOffset, yOffset;
    private Pose2d localAutoPose, localRobotPose;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     * @param isBlue whether the robot is on the blue alliance
     */
    public SwervePathFollowerStep(PathPlannerTrajectory pathData, ChoreoTrajectory traj, SwerveDriveTemplate drive, boolean isBlue) {
        this.pathData = pathData;
        m_drive = drive;
        pathtraj = traj;
        
        this.isBlue = isBlue;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        if (timer.get() >= pathData.getTotalTimeSeconds()) {
            m_drive.setAutoValues(0, -pathData.getEndState().poseMeters.getRotation().getDegrees(),0,0,0);
            SmartDashboard.putNumber("auto final time", timer.get());
            setFinished();
        } else {
            localRobotPose = m_drive.returnPose(getVelocity());
            localAutoPose = pathData.sample(timer.get()).poseMeters;
            yOffset = -(localRobotPose.getX() - localAutoPose.getX());
            if (isBlue){
                xOffset = localRobotPose.getY() - localAutoPose.getY();
            } else {
                xOffset = localRobotPose.getY() - (8.016 - localAutoPose.getY());
            }
            //update values the robot is tracking to
            m_drive.setAutoValues( getVelocity(),getHeading(), getAccel(), 2.0*xOffset,2.0*yOffset );
            
            }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

    public double getVelocity(){
        //return pathData.sample(timer.get()).velocityMetersPerSecond * mToIn;
        return Math.hypot(pathtraj.sample(timer.get()).velocityX, pathtraj.sample(timer.get()).velocityY);
    }
    public double getHeading(){
        //if (isBlue) return (-pathData.sample(timer.get()).poseMeters.getRotation().getDegrees() + 360)%360;
        //else return (pathData.sample(timer.get()).poseMeters.getRotation().getDegrees()+360)%360;
        return pathtraj.sample(timer.get()).heading*180/Math.PI;
    }
    public double getAccel(){
        return pathData.sample(timer.get()).accelerationMetersPerSecondSq * mToIn * mToIn;
    }
}
