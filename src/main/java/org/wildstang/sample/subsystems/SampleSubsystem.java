package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.sample.robot.WSInputs;
import org.wildstang.sample.robot.WSOutputs;

/**
 * Sample Subsystem that controls a motor with a joystick.
 * @author Liam
 */
public class SampleSubsystem implements Subsystem {

    WsPhoenix motor;
    WsJoystickAxis js;

    @Override
    public void init() {
        //motor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.TEST_MOTOR);
        js = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
    }

    @Override
    public void update() {
        motor.setValue(js.getValue());
    }

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void selfTest() {

    }

    @Override
    public void resetState() {

    }

    @Override
    public String getName() {
        return "Sample";
    }
}