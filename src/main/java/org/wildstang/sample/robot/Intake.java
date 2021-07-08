package org.wildstang.sample.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;

public class Intake implements Subsystem {

    WsPhoenix intakeMotor;

    WsJoystickAxis forwardAxis;
    WsJoystickAxis reverseAxis;

    double intakeSpeed;

    @Override
    public void init() {
        intakeMotor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.INTAKE_MOTOR);

        forwardAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_TRIGGER);
        forwardAxis.addInputListener(this);
        reverseAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.MANIPULATOR_LEFT_TRIGGER);
        reverseAxis.addInputListener(this);
    }

    @Override
    public void update() {
        intakeMotor.setSpeed(intakeSpeed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == forwardAxis) {
            intakeSpeed = forwardAxis.getValue();
        }
        else if (source == reverseAxis) {
            intakeSpeed = reverseAxis.getValue();
        }
    }

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {
        intakeSpeed = 0;
        intakeMotor.setBrake();
    }

    @Override
    public String getName() {
        return "Intake";
    }
}