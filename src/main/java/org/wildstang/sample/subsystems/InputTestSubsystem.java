package org.wildstang.sample.subsystems;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.sample.robot.WSInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Subsystem used to test all controller buttons and axes.
 * Each Input has a SmartDashboard widget for its state.
 * A pre-arranged Shuffleboard config can be found at /shuffleboards/input-test.json
 * @author Liam
 */
public class InputTestSubsystem implements Subsystem {

    WSInputs axes[] = {
        WSInputs.DRIVER_LEFT_JOYSTICK_X,
        WSInputs.DRIVER_LEFT_JOYSTICK_Y,
        WSInputs.DRIVER_LEFT_TRIGGER,
        WSInputs.DRIVER_RIGHT_JOYSTICK_X,
        WSInputs.DRIVER_RIGHT_JOYSTICK_Y,
        WSInputs.DRIVER_RIGHT_TRIGGER,

        WSInputs.MANIPULATOR_LEFT_JOYSTICK_X,
        WSInputs.MANIPULATOR_LEFT_JOYSTICK_Y,
        WSInputs.MANIPULATOR_LEFT_TRIGGER,
        WSInputs.MANIPULATOR_RIGHT_JOYSTICK_X,
        WSInputs.MANIPULATOR_RIGHT_JOYSTICK_Y,
        WSInputs.MANIPULATOR_RIGHT_TRIGGER
    };

    WSInputs buttons[] = {
        WSInputs.DRIVER_DPAD_LEFT,
        WSInputs.DRIVER_DPAD_DOWN,
        WSInputs.DRIVER_DPAD_UP,
        WSInputs.DRIVER_DPAD_RIGHT,

        WSInputs.MANIPULATOR_DPAD_LEFT,
        WSInputs.MANIPULATOR_DPAD_DOWN,
        WSInputs.MANIPULATOR_DPAD_UP,
        WSInputs.MANIPULATOR_DPAD_RIGHT,
        
        WSInputs.DRIVER_FACE_LEFT,
        WSInputs.DRIVER_FACE_DOWN,
        WSInputs.DRIVER_FACE_UP,
        WSInputs.DRIVER_FACE_RIGHT,
        WSInputs.DRIVER_LEFT_SHOULDER,
        WSInputs.DRIVER_RIGHT_SHOULDER,
        WSInputs.DRIVER_SELECT,
        WSInputs.DRIVER_START,
        WSInputs.DRIVER_LEFT_JOYSTICK_BUTTON,
        WSInputs.DRIVER_RIGHT_JOYSTICK_BUTTON,
        
        WSInputs.MANIPULATOR_FACE_LEFT,
        WSInputs.MANIPULATOR_FACE_DOWN,
        WSInputs.MANIPULATOR_FACE_UP,
        WSInputs.MANIPULATOR_FACE_RIGHT,
        WSInputs.MANIPULATOR_LEFT_SHOULDER,
        WSInputs.MANIPULATOR_RIGHT_SHOULDER,
        WSInputs.MANIPULATOR_SELECT,
        WSInputs.MANIPULATOR_START,
        WSInputs.MANIPULATOR_LEFT_JOYSTICK_BUTTON,
        WSInputs.MANIPULATOR_RIGHT_JOYSTICK_BUTTON
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
        for (WSInputs axis : axes) {
            putAxis((WsJoystickAxis) Core.getInputManager().getInput(axis));
        }
        for (WSInputs button : buttons) {
            putButton((DigitalInput) Core.getInputManager().getInput(button));
        }
    }

    @Override
    public void inputUpdate(Input source) {}

    @Override
    public void selfTest() {}

    @Override
    public void resetState() {}

    @Override
    public String getName() {
        return "Input Test";
    }
}