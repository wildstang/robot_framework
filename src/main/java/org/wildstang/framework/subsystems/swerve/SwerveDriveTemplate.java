package org.wildstang.framework.subsystems.swerve;

import org.wildstang.framework.subsystems.Subsystem;

public abstract class SwerveDriveTemplate implements Subsystem{
    
    public abstract void setAutoValues(double position, double velocity, double heading);

    public abstract void resetDriveEncoders();

    public abstract void setAutoHeading(double headingTarget);

    public abstract void setGyro(double degrees);

    public abstract void setToAuto();

    public abstract void setToTeleop();

}
