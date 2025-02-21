package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

public class VisionOnStep extends AutoStep{

    private SwerveDrive swerve;
    private boolean on;

    public VisionOnStep(boolean isOn){
        on = isOn;
    }
    public void update(){
        swerve.setVisionAuto(on);
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }
    public String toString(){
        return "Vision in auto";
    }
    
}
