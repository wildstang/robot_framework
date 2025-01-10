package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

public class AlignOnStep extends AutoStep{

    private SwerveDrive swerve;
    private boolean on;

    public AlignOnStep(boolean isOn){
        on = isOn;
    }
    public void update(){
        swerve.setAutoAlign(on);
        this.setFinished();
    }
    public void initialize(){
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }
    public String toString(){
        return "Align in auto";
    }
    
}
