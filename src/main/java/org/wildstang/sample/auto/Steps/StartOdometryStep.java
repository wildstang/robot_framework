package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class StartOdometryStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;
    private boolean color;//true for blue, false for red

    public StartOdometryStep(double X, double Y, double pathHeading, boolean allianceColor){
        x = X;
        y = Y;
        heading = pathHeading;
        color = allianceColor;
    }
    public void update(){
        if (color){
            swerve.setOdo(new Pose2d(new Translation2d(x, y), new Rotation2d(Math.toRadians(360.0-heading))));
        } else {
            swerve.setOdo(new Pose2d(new Translation2d(x, 8.016-y), new Rotation2d(Math.toRadians(360.0-heading))));
        }
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }
    public String toString(){
        return "Start Odometry";
    }
    
}
