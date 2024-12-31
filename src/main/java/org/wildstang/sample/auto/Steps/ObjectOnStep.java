package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

public class ObjectOnStep extends AutoStep{

    private SwerveDrive swerve;
    private boolean isOn;

    public ObjectOnStep(boolean isOn){
        this.isOn = isOn;
    }
    @Override
    public void initialize() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void update() {
        swerve.setAutoObject(isOn);
        setFinished();
    }

    @Override
    public String toString() {
        return "Object On Step";
    }
    
}
