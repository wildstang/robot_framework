package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

public class SetGyroStep extends AutoStep {

    private SwerveDriveTemplate m_drive;
    private double heading;

    /** DEPRECATED use AutoSetupStep instead
     *  sets the gyro value to be the given argument value
     * @param heading value you want the gyro to currently read
     * @param drive the swerveDrive subsystem
     */
    public SetGyroStep(double heading, SwerveDriveTemplate drive) {
        this.heading = heading;
        m_drive = drive;
    }

    @Override
    public void initialize() {
        m_drive.setToAuto();
        m_drive.setGyro(heading);
        m_drive.setAutoHeading(heading);
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
