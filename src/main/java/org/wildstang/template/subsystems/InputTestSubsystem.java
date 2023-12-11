package org.wildstang.template.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.template.robot.WsInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Subsystem used to test all controller buttons and axes.
 * Each Input has a SmartDashboard widget for its state.
 * A pre-arranged Shuffleboard config can be found at
 * /shuffleboards/input-test.json
 * 
 * @author Liam
 */
public class InputTestSubsystem implements Subsystem {

    public static final String NAME = "Input Test";

    WsInputs axes[] = {
            WsInputs.DRIVER_LEFT_JOYSTICK_X,
            WsInputs.DRIVER_LEFT_JOYSTICK_Y,
            WsInputs.DRIVER_LEFT_TRIGGER,
            WsInputs.DRIVER_RIGHT_JOYSTICK_X,
            WsInputs.DRIVER_RIGHT_JOYSTICK_Y,
            WsInputs.DRIVER_RIGHT_TRIGGER,

            WsInputs.OPERATOR_LEFT_JOYSTICK_X,
            WsInputs.OPERATOR_LEFT_JOYSTICK_Y,
            WsInputs.OPERATOR_LEFT_TRIGGER,
            WsInputs.OPERATOR_RIGHT_JOYSTICK_X,
            WsInputs.OPERATOR_RIGHT_JOYSTICK_Y,
            WsInputs.OPERATOR_RIGHT_TRIGGER
    };

    WsInputs buttons[] = {
            WsInputs.DRIVER_DPAD_LEFT,
            WsInputs.DRIVER_DPAD_DOWN,
            WsInputs.DRIVER_DPAD_UP,
            WsInputs.DRIVER_DPAD_RIGHT,

            WsInputs.OPERATOR_DPAD_LEFT,
            WsInputs.OPERATOR_DPAD_DOWN,
            WsInputs.OPERATOR_DPAD_UP,
            WsInputs.OPERATOR_DPAD_RIGHT,

            WsInputs.DRIVER_FACE_LEFT,
            WsInputs.DRIVER_FACE_DOWN,
            WsInputs.DRIVER_FACE_UP,
            WsInputs.DRIVER_FACE_RIGHT,
            WsInputs.DRIVER_LEFT_SHOULDER,
            WsInputs.DRIVER_RIGHT_SHOULDER,
            WsInputs.DRIVER_SELECT,
            WsInputs.DRIVER_START,
            WsInputs.DRIVER_LEFT_JOYSTICK_BUTTON,
            WsInputs.DRIVER_RIGHT_JOYSTICK_BUTTON,

            WsInputs.OPERATOR_FACE_LEFT,
            WsInputs.OPERATOR_FACE_DOWN,
            WsInputs.OPERATOR_FACE_UP,
            WsInputs.OPERATOR_FACE_RIGHT,
            WsInputs.OPERATOR_LEFT_SHOULDER,
            WsInputs.OPERATOR_RIGHT_SHOULDER,
            WsInputs.OPERATOR_SELECT,
            WsInputs.OPERATOR_START,
            WsInputs.OPERATOR_LEFT_JOYSTICK_BUTTON,
            WsInputs.OPERATOR_RIGHT_JOYSTICK_BUTTON
    };

    @Override
    public void init() {
    }

    public void putAxis(AnalogInput axis) {
        SmartDashboard.putNumber(axis.getName(), axis.getValue());
    }

    public void putButton(DigitalInput button) {
        SmartDashboard.putBoolean(button.getName(), button.getValue());
    }

    @Override
    public void update() {
        for (WsInputs axis : axes) {
            putAxis((WsJoystickAxis) Core.getInputManager().getInput(axis));
        }
        for (WsInputs button : buttons) {
            putButton((DigitalInput) Core.getInputManager().getInput(button));
        }
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
        return NAME;
    }
}