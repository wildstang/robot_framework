package org.wildstang.year2021C.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2021C.robot.WSInputs;
import org.wildstang.year2021C.robot.WSOutputs;

public class Outtake implements Subsystem {

    WsPhoenix outtakeMotor;

    WsJoystickButton forwardButton;
    WsJoystickButton reverseButton;

    double outtakeSpeed;

    @Override
    public void init() {
        outtakeMotor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.OUTTAKE_MOTOR);

        forwardButton = (WsJoystickButton) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        forwardButton.addInputListener(this);
        reverseButton = (WsJoystickButton) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_SHOULDER);
        reverseButton.addInputListener(this);
    }

    @Override
    public void update() {
        outtakeMotor.setSpeed(outtakeSpeed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == forwardButton) {
            outtakeSpeed = forwardButton.getValue() ? 1 : 0;
        }
        else if (source == reverseButton) {
            outtakeSpeed = reverseButton.getValue() ? -1 : 0;
        }
    }

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {
        outtakeSpeed = 0;
        outtakeMotor.setBrake();
    }

    @Override
    public String getName() {
        return "Outtake";
    }
}