package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.sample.robot.WSSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

public class SetGyroStep extends AutoStep {

    private SwerveDriveTemplate m_drive;
    private double heading;

    /** sets the gyro value to be the given argument value
     * @param heading value you want the gyro to currently read
     * @param drive the swerveDrive subsystem
     */
    public SetGyroStep(double heading, SwerveDriveTemplate drive) {
        this.heading = heading;
        m_drive = drive;
    }

    @Override
    public void initialize() {
        m_drive.setGyro(heading);
    }

    @Override
    public void update() {
        setFinished(true);
    }

    @Override
    public String toString() {
        return "Set Gyro";
    }

}
