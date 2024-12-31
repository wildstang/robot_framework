package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.AutoSetupStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

// Test swerve auto program, named per Arms
public class Jubilee extends AutoProgram {

    @Override
    protected void defineSteps() {
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new AutoSetupStep(1.5, 5.5, 0, Alliance.Red));
        addStep(new SwervePathFollowerStep("Circle", swerve));
        addStep(new SwervePathFollowerStep("Circle", swerve));                
        addStep(new SwervePathFollowerStep("Circle", swerve));
    }

    @Override
    public String toString() {
        return "Jubilee";
    }
    
}
