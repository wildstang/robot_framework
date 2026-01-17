package org.wildstang.sample.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.hardware.JoystickConstants;
import org.wildstang.hardware.roborio.inputs.config.WsJSButtonInputConfig;
import org.wildstang.hardware.roborio.inputs.config.WsDPadButtonInputConfig;
import org.wildstang.hardware.roborio.inputs.config.WsDigitalInputConfig;
import org.wildstang.hardware.roborio.inputs.config.WsJSJoystickInputConfig;

/**
 * Input mappings are stored here.
 * We currently use two Xbox controller for input, driver and operator, plus additional sensors.
 * Below each button, axis, and sensor is enumerated with their appropriated IDs.
 * Unclaimed inputs should have a name representing the input.
 * Claim inputs by changing the name to represent the output.
 */
public enum WsInputs implements Inputs {
    
    //***************************************************************
    //      Driver and Operator Controller Button Locations
    //***************************************************************
    //
    //    +-------------------------------------------------------+
    //  +  +---------+                                 +---------+  +       
    //  |  |  RIGHT  |           TRIGGERS              |  LEFT   |  |       
    //  |  +---------+                                 +---------+  |       
    //  |      			                                            |   
    //  |  +---------+                                 +---------+  |       
    //  |  |    4    |           SHOULDERS             |    5    |  |
    //  +  +---------+                                 +---------+  +
    //    +-------------------------------------------------------+
    //  
    //    +-------------------------------------------------------+
    //   /    +--+                 [FRONT]                         \
    //  +     |YU|                                         (3)      +       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |  |XL    XR|        |  6 |  (X)  |  7 |       (2)     (1)  |       
    //  |  +--+  +--+        +----+       +----+                    | 
    //  |     |YD|                                         (0)      |       
    //  |     +--+     +--+          (X)          +--+              |
    //  |             /    \                     /    \             |
    //  |            |   8  |                   |   9  |            |
    //  |             \    /                     \    /             |
    //  +              +--+                       +--+              +
    //   \                                                         /
    //    \            +-----------------------------+            /
    //     \          /                               \          /
    //      \        /                                 \        /
    //       \      /                                   \      /
    //        +----+                                     +----+
    //
    //
    // ********************************
    // Driver Enums
    // ********************************
    //
    // ---------------------------------
    // Driver Joysticks
    // ---------------------------------
    DRIVER_LEFT_JOYSTICK_VERTICAL  ("Driver left joystick vertical",  new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y)),
    DRIVER_LEFT_JOYSTICK_HORIZONTAL  ("Driver left joystick horizontal",  new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_X)),
    DRIVER_RIGHT_JOYSTICK_VERTICAL ("Driver right joystick vertical", new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_Y)),
    DRIVER_RIGHT_JOYSTICK_HORIZONTAL ("Driver right joystick horizontal", new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X)),
    
    // ---------------------------------
    // Driver DPAD Buttons
    // ---------------------------------
    DRIVER_DPAD_DOWN  ("Driver dpad down",  new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_Y_DOWN)),
    DRIVER_DPAD_LEFT  ("Driver dpad left",  new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_X_LEFT)),
    DRIVER_DPAD_RIGHT ("Driver dpad right", new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_X_RIGHT)),
    DRIVER_DPAD_UP    ("Driver dpad up",    new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_Y_UP)),

    // ---------------------------------
    // Driver Buttons
    // --------------------------------- 
    DRIVER_FACE_DOWN             ("Driver face down",             new WsJSButtonInputConfig(0, 0)),
    DRIVER_FACE_LEFT             ("Driver face left",             new WsJSButtonInputConfig(0, 2)),
    DRIVER_FACE_RIGHT            ("Driver face right",            new WsJSButtonInputConfig(0, 1)),
    DRIVER_FACE_UP               ("Driver face up",               new WsJSButtonInputConfig(0, 3)),
    DRIVER_LEFT_SHOULDER         ("Driver left shoulder",         new WsJSButtonInputConfig(0, 4)),
    DRIVER_RIGHT_SHOULDER        ("Driver right shoulder",        new WsJSButtonInputConfig(0, 5)),
    DRIVER_SELECT                ("Driver select",                new WsJSButtonInputConfig(0, 6)),
    DRIVER_START                 ("Driver start",                 new WsJSButtonInputConfig(0, 7)),
    DRIVER_LEFT_JOYSTICK_BUTTON  ("Driver left joystick button",  new WsJSButtonInputConfig(0, 8)),
    DRIVER_RIGHT_JOYSTICK_BUTTON ("Driver right joystick button", new WsJSButtonInputConfig(0, 9)),

    // ---------------------------------
    // Driver Triggers
    // ---------------------------------
    DRIVER_LEFT_TRIGGER  ("Driver left trigger",  new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_TRIGGER)),
    DRIVER_RIGHT_TRIGGER ("Driver right trigger", new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_TRIGGER)),

    // ---------------------------------
    // Operator Joysticks
    // ---------------------------------
    OPERATOR_LEFT_JOYSTICK_VERTICAL  ("Operator left joystick vertical",  new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_Y)),
    OPERATOR_LEFT_JOYSTICK_HORIZONTAL  ("Operator left joystick horizontal",  new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_X)),
    OPERATOR_RIGHT_JOYSTICK_VERTICAL ("Operator right joystick vertical", new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_Y)),
    OPERATOR_RIGHT_JOYSTICK_HORIZONTAL ("Operator right joystick horizontal", new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_X)),

    // ---------------------------------
    // Operator DPAD Buttons
    // ---------------------------------
    OPERATOR_DPAD_DOWN  ("Operator dpad down",  new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_Y_DOWN)),
    OPERATOR_DPAD_LEFT  ("Operator dpad left",  new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_X_LEFT)),
    OPERATOR_DPAD_RIGHT ("Operator dpad right", new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_X_RIGHT)),
    OPERATOR_DPAD_UP    ("Operator dpad up",    new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_Y_UP)),

    // ---------------------------------
    // Operator Buttons
    // ---------------------------------
    OPERATOR_FACE_DOWN             ("Operator face down",             new WsJSButtonInputConfig(1, 0)),
    OPERATOR_FACE_LEFT             ("Operator face left",             new WsJSButtonInputConfig(1, 2)),
    OPERATOR_FACE_RIGHT            ("Operator face right",            new WsJSButtonInputConfig(1, 1)),
    OPERATOR_FACE_UP               ("Operator face up",               new WsJSButtonInputConfig(1, 3)),
    OPERATOR_LEFT_SHOULDER         ("Operator left shoulder",         new WsJSButtonInputConfig(1, 4)),
    OPERATOR_RIGHT_SHOULDER        ("Operator right shoulder",        new WsJSButtonInputConfig(1, 5)),
    OPERATOR_SELECT                ("Operator select",                new WsJSButtonInputConfig(1, 6)),
    OPERATOR_START                 ("Operator start",                 new WsJSButtonInputConfig(1, 7)),
    OPERATOR_LEFT_JOYSTICK_BUTTON  ("Operator left joystick button",  new WsJSButtonInputConfig(1, 8)),
    OPERATOR_RIGHT_JOYSTICK_BUTTON ("Operator right joystick button", new WsJSButtonInputConfig(1, 9)),

    // ---------------------------------
    // Operator Triggers
    // ---------------------------------
    OPERATOR_LEFT_TRIGGER  ("Operator left trigger",  new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_TRIGGER)),
    OPERATOR_RIGHT_TRIGGER ("Operator right trigger", new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_TRIGGER)),

    // ********************************
    // Digital IOs
    // ********************************

    // -------------------------------
    // Networked sensors
    // -------------------------------

    // ********************************
    // Others ...
    // ********************************
            
    ; // end of enum
    
    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private final String m_name;
    private InputConfig m_config = null;

    /**
     * Initialize a new Input.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for InputType.
     */
    WsInputs(String p_name, InputConfig p_config) {
        m_name = p_name;
        m_config = p_config;
    }

    /**
     * Returns the name mapped to the Input.
     * @return Name mapped to the Input.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the config of Input for the enumeration.
     * @return InputConfig of enumeration.
     */
    public InputConfig getConfig() {
        return m_config;
    }

    /**
     * Returns the actual Input object from the InputManager
     * @return The corresponding input.
     */
    public Input get() {
        return Core.getInputManager().getInput(this);
    }
}
