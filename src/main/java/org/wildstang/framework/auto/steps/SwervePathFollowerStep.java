package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import com.pathplanner.lib.PathPlannerTrajectory;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwervePathFollowerStep extends AutoStep {

    private static final double ftToIn = 12;
    private static final double mToIn = 30.3701;
    private static final int positionP = 7;
    //private static final int velocityP = 8;
    private static final int headingP = 15;
    //dt, x, y, leftPos, leftVel, leftAcc, leftJer, centerPos, centerVel, centerAcc, centerJer, rightPos, rightVel, rightAcc, rightJer, heading
    //0   1  2    3          4       5        6          7          8         9          10        11         12       13        14       15

    private SwerveDriveTemplate m_drive;
    private PathPlannerTrajectory pathData;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     */
    public SwervePathFollowerStep(PathPlannerTrajectory pathData, SwerveDriveTemplate drive) {
        this.pathData = pathData;
        m_drive = drive;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        m_drive.resetDriveEncoders();
        m_drive.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        if (timer.get() >= pathData.getTotalTimeSeconds()) {
            m_drive.setAutoValues(0,0, -pathData.getEndState().poseMeters.getRotation().getDegrees());
            setFinished();
        } else {
            SmartDashboard.putNumber("Auto Time", timer.get());
            //update values the robot is tracking to
            m_drive.setAutoValues(0, pathData.sample(timer.get()).velocityMetersPerSecond * mToIn, -pathData.sample(timer.get()).poseMeters.getRotation().getDegrees());
            }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

}
