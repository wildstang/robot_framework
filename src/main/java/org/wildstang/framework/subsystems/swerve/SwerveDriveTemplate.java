package org.wildstang.framework.subsystems.swerve;

import org.wildstang.framework.subsystems.Subsystem;

import edu.wpi.first.math.geometry.Pose2d;

public abstract class SwerveDriveTemplate implements Subsystem{

    public abstract void setAutoHeading(double headingTarget);

    public abstract void setGyro(double degrees);

    public abstract void setToAuto();

    public abstract void setToTeleop();

    public abstract void setAutoValues(double xVelocity, double yVelocity, double xAccel, double yAccel, Pose2d target);

}
