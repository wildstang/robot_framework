package org.wildstang.framework.auto.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import choreo.*;
import choreo.trajectory.Trajectory;

import com.google.gson.Gson;

public class SwervePathFollowerStep extends AutoStep {

    private static final double mToIn = 39.3701;
    private SwerveDriveTemplate m_drive;
    private Trajectory pathTraj;

    // x and y field relative
    private Pose2d fieldAutoPose, fieldRobotPose;
    private double xOffset, yOffset;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     * @param isBlue whether the robot is on the blue alliance
     */
    public SwervePathFollowerStep(String pathData, SwerveDriveTemplate drive) {
        this.pathtraj = getTraj(pathData);
        m_drive = drive;
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
        if (timer.get() >= pathtraj.getTotalTime()) {
            m_drive.setAutoValues(0.0,0.0,0.0,0.0);
            SmartDashboard.putNumber("auto final time", timer.get());
            setFinished();
        } else {
            ChoreoTrajectoryState sample = pathtraj.sample(timer.get());
            
            
            fieldRobotPose = m_drive.returnPose();
            fieldAutoPose = sample.getPose();

            xOffset = -fieldAutoPose.getY() + (Core.isBlue() ? fieldRobotPose.getY() : 8.016-fieldRobotPose.getY());
            yOffset = fieldAutoPose.getX() - fieldRobotPose.getY();
            SmartDashboard.putNumber("xOffset", xOffset);
            SmartDashboard.putNumber("yOffset", yOffset);

            m_drive.setAutoHeading(getHeading());
            m_drive.setAutoValues((Core.isBlue() ? -1 : 1) * sample.velocityY * mToIn, sample.velocityX * mToIn, xOffset, yOffset);
            SmartDashboard.putNumber("PF local X", fieldRobotPose.getX());
            SmartDashboard.putNumber("PF path X", fieldAutoPose.getX());
            }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

    public double getHeading(){
        if (Core.isBlue()) return ((-pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360;
        else return ((pathtraj.sample(timer.get()).heading*180/Math.PI)+360)%360;
    }

    public ChoreoTrajectory getTraj(String fileName){
        Gson gson = new Gson();
        var tempfile = Filesystem.getDeployDirectory();
        var traj_dir = new File(tempfile, "choreo");

        var traj_file = new File(traj_dir, fileName + ".traj");
        try {
            var reader = new BufferedReader(new FileReader(traj_file));
            return  gson.fromJson(reader, ChoreoTrajectory.class);
        } catch (Exception ex) {
            DriverStation.reportError("Choreo Trajectory get Error", ex.getStackTrace());
        }return new ChoreoTrajectory();
    }
}
