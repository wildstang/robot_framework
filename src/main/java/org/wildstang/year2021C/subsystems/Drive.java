package org.wildstang.year2021C.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.year2021C.robot.WSInputs;
import org.wildstang.year2021C.robot.WSOutputs;

public class Drive implements Subsystem {

    final double DEADZONE = 0.1;

    WsPhoenix leftMotor;
    WsPhoenix rightMotor;

    WsJoystickAxis throttleAxis;
    WsJoystickAxis headingAxis;
    WsJoystickAxis rotateLeftAxis;
    WsJoystickAxis rotateRightAxis;

    WsJoystickButton coastButton;

    double throttle;
    double heading;
    double rotation;

    @Override
    public void init() {
        leftMotor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.DRIVE_LEFT_MOTOR);
        rightMotor = (WsPhoenix) Core.getOutputManager().getOutput(WSOutputs.DRIVE_RIGHT_MOTOR);

        throttleAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        throttleAxis.addInputListener(this);
        headingAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X);
        headingAxis.addInputListener(this);
        rotateLeftAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_TRIGGER);
        rotateLeftAxis.addInputListener(this);
        rotateRightAxis = (WsJoystickAxis) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_TRIGGER);
        rotateRightAxis.addInputListener(this);
        
        coastButton = (WsJoystickButton) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_RIGHT);
        coastButton.addInputListener(this);
    }

    @Override
    public void update() {
        double leftMotorSpeed = (heading < 0 ? 1 + heading : 1) * throttle - rotation;
        double rightMotorSpeed = (heading > 0 ? 1 - heading : 1) * throttle + rotation;

        leftMotor.setSpeed(leftMotorSpeed);
        rightMotor.setSpeed(rightMotorSpeed);
    }

    @Override
    public void inputUpdate(Input source) {
        if (source == throttleAxis) {
            throttle = throttleAxis.getValue();
            throttle = Math.abs(throttle) > DEADZONE ? throttle : 0;
        }
        else if (source == headingAxis) {
            heading = headingAxis.getValue();
            heading = Math.abs(heading) > DEADZONE ? heading : 0;
        }
        else if (source == rotateLeftAxis) {
            rotation = rotateLeftAxis.getValue();
        }
        else if (source == rotateRightAxis) {
            rotation = rotateRightAxis.getValue();
        }
        else if (source == coastButton) {
            if (coastButton.getValue()) {
                leftMotor.setCoast();
                rightMotor.setCoast();
            }
            else {
                leftMotor.setBrake();
                rightMotor.setBrake();
            }
        }
    }

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {
        throttle = 0;
        heading = 0;
        rotation = 0;
        leftMotor.setBrake();
        rightMotor.setBrake();
    }

    @Override
    public String getName() {
        return "Drive";
    }
}