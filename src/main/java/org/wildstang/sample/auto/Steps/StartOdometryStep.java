package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

/**
 * AutoStep used to configure the robot's odometry before path following.
 */
public class StartOdometryStep extends AutoStep {

    private double x, y, heading;
    private SwerveDrive swerve;
    private boolean isBlue;

    public StartOdometryStep(double X, double Y, double pathHeading, boolean isBlueAlliance) {
        x = X;
        y = Y;
        heading = pathHeading;
        isBlue = isBlueAlliance;
    }

    @Override
    public void update() {
        Rotation2d rotation = new Rotation2d(Math.toRadians(360.0 - heading));
        if (isBlue) {
            swerve.setOdo(new Pose2d(new Translation2d(x, y), rotation));
        } else {
            swerve.setOdo(new Pose2d(new Translation2d(x, 8.016 - y), rotation));
        }
        this.setFinished();
    }

    @Override
    public void initialize() {
        swerve = (SwerveDrive) WsSubsystems.SWERVE_DRIVE.get();
    }

    @Override
    public String toString() {
        return "Start Odometry";
    }

}
