package org.wildstang.sample.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.LED.Blinkin.BlinkinValues;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;


public class LedController implements Subsystem {

    private SwerveDrive swerve;
    private Blinkin led;
    private BlinkinValues color;

    @Override
    public void update(){
        led.setColor(color);
    }

    @Override
    public void inputUpdate(Input source) {
    }

    @Override
    public void initSubsystems() {      
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void init() {
        
        //Outputs
        led = new Blinkin(0);
        color = BlinkinValues.RAINBOW_RAINBOW_PALETTE;
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
    }

    @Override
    public String getName() {
        return "Led Controller";
    }
}
