package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.AutoParallelStepGroup;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.auto.Steps.StartOdometryStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import com.choreo.lib.Choreo;


public class TestProgram extends AutoProgram{
    
    protected void defineSteps(){
        SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        addStep(new SetGyroStep(180.0, swerve));
        AutoParallelStepGroup group1 = new AutoParallelStepGroup();
        group1.addStep(new PathHeadingStep(180.0, swerve));
        addStep(group1);
        addStep(new StartOdometryStep(3.0, 5.0, 180.0, true));
        addStep(new SwervePathFollowerStep(Choreo.getTrajectory("ChoreoTest"), swerve, true));
    }

    public String toString(){
        return "Test Program";
    }
}
