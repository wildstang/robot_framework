package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class SetAlignStep extends AutoStep{

    private SwerveDrive swerve;
    private Pose2d odoPose;
    private double heading;

    public SetAlignStep(double x, double y, double heading){
        odoPose = new Pose2d(x,y, Rotation2d.fromDegrees(360 - heading));
        this.heading = heading;
    }

    @Override
    public void initialize() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void update() {
        swerve.setAutoValues(0,0,0, 0, odoPose);
        swerve.setAutoHeading(heading);
        setFinished();
    }

    @Override
    public String toString() {
        return "Set Align Step";
    }
    
}
