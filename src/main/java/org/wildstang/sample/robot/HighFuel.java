package org.wildstang.sample.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;

public class HighFuel implements Subsystem {

    WsPhoenix armMotor;

    WsJoystickButton forwardButton;
    WsJoystickButton reverseButton;

    double armSpeed;

    @Override
    public void init() {
        armMotor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.HIGHFUEL_MOTOR);

        forwardButton = (WsJoystickButton) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_RIGHT);
        forwardButton.addInputListener(this);
        reverseButton = (WsJoystickButton) Core.getInputManager().getInput(WSInputs.MANIPULATOR_FACE_LEFT);
        reverseButton.addInputListener(this);
    }

    @Override
    public void update() {
        armMotor.setSpeed(armSpeed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == forwardButton) {
            armSpeed = forwardButton.getValue() ? 0.25 : 0;
        }
        else if (source == reverseButton) {
            armSpeed = reverseButton.getValue() ? -0.1 : 0;
        }
    }

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {
        armSpeed = 0;
        armMotor.setBrake();
    }

    @Override
    public String getName() {
        return "High Fuel";
    }
}