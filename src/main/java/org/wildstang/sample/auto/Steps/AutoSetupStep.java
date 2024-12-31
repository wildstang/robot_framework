package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.LED.LedController;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.swerve.SwerveDrive.driveType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class AutoSetupStep extends AutoStep{

    private double x, y, heading;
    private SwerveDrive swerve;

    // Setup based on starting Choreo position
    public AutoSetupStep(double x, double y, double pathHeading, Alliance alliance){
        this.x = x;
        this.y = y;
        heading = pathHeading;
        Core.setAlliance(alliance);
        LedController led = (LedController) Core.getSubsystemManager().getSubsystem(WsSubsystems.LED);
        led.setAuto();
    }

    public void update(){
        double difference = Math.min(Math.abs(swerve.getGyroAngle() - heading), Math.abs(360 - (swerve.getGyroAngle() - heading)));
        // Gyro reset and reads within 1 degree of what we told it to
        if (difference < 1) {
            // Sets odometry field relative, flipping for red
            if (Core.isBlue()){
                swerve.setOdo(new Pose2d(new Translation2d(x, y), new Rotation2d(Math.toRadians(360.0-heading))));
            } else {
                swerve.setOdo(new Pose2d(new Translation2d(x, 8.016-y), new Rotation2d(Math.toRadians(360.0-heading))));
            }
            this.setFinished();
        }
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        swerve.setAutoValues(0, 0, 0, 0);
        swerve.setAutoHeading(heading);
        swerve.setGyro(heading);
    }
    public String toString(){
        return "Auto Setup Step";
    }
    
}
