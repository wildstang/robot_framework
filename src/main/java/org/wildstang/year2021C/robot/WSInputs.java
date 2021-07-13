package org.wildstang.year2021C.robot;

import org.wildstang.framework.core.Inputs;
import org.wildstang.framework.hardware.InputConfig;
import org.wildstang.hardware.JoystickConstants;
import org.wildstang.hardware.roborio.inputs.config.WsJSButtonInputConfig;
import org.wildstang.hardware.roborio.inputs.config.WsDPadButtonInputConfig;
import org.wildstang.hardware.roborio.inputs.config.WsJSJoystickInputConfig;

/**
 * Input mappings are stored here.
 * We currently use two Xbox controller for input, driver and manipulator, plus additional sensors.
 * Below each button, axis, and sensor is enumerated with their appropriated IDs.
 * Unclaimed inputs should have a name representing the input.
 * Claim inputs by changing the name to represent the output.
 */
public enum WSInputs implements Inputs {
    
    //***************************************************************
    //      Driver and Manipulator Controller Button Locations
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
    DRIVER_LEFT_JOYSTICK_Y  ("Drive Speed",             new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_Y),  false),
    DRIVER_LEFT_JOYSTICK_X  ("Driver left joystick x",  new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_JOYSTICK_X),  false),
    DRIVER_RIGHT_JOYSTICK_Y ("Driver right joystick y", new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_Y), false),
    DRIVER_RIGHT_JOYSTICK_X ("Drive Heading",           new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_JOYSTICK_X), false),
    
    // ---------------------------------
    // Driver DPAD Buttons
    // ---------------------------------
    DRIVER_DPAD_DOWN  ("Driver dpad down",  new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_Y_DOWN),  false),
    DRIVER_DPAD_LEFT  ("Driver dpad left",  new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_X_LEFT),  false),
    DRIVER_DPAD_RIGHT ("Driver dpad right", new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_X_RIGHT), false),
    DRIVER_DPAD_UP    ("Driver dpad up",    new WsDPadButtonInputConfig(0, JoystickConstants.DPAD_Y_UP),    false),

    // ---------------------------------
    // Driver Buttons
    // --------------------------------- 
    DRIVER_FACE_DOWN             ("Driver face down",             new WsJSButtonInputConfig(0, 0), false),
    DRIVER_FACE_LEFT             ("Driver face left",             new WsJSButtonInputConfig(0, 2), false),
    DRIVER_FACE_RIGHT            ("Coast",                        new WsJSButtonInputConfig(0, 1), false),
    DRIVER_FACE_UP               ("Driver face up",               new WsJSButtonInputConfig(0, 3), false),
    DRIVER_LEFT_SHOULDER         ("Driver left shoulder",         new WsJSButtonInputConfig(0, 4), false),
    DRIVER_RIGHT_SHOULDER        ("Driver right shoulder",        new WsJSButtonInputConfig(0, 5), false),
    DRIVER_SELECT                ("Driver select",                new WsJSButtonInputConfig(0, 6), false),
    DRIVER_START                 ("Driver start",                 new WsJSButtonInputConfig(0, 7), false),
    DRIVER_LEFT_JOYSTICK_BUTTON  ("Driver left joystick button",  new WsJSButtonInputConfig(0, 8), false),
    DRIVER_RIGHT_JOYSTICK_BUTTON ("Driver right joystick button", new WsJSButtonInputConfig(0, 9), false),

    // ---------------------------------
    // Driver Triggers
    // ---------------------------------
    DRIVER_LEFT_TRIGGER  ("Rotate Left",  new WsJSJoystickInputConfig(0, JoystickConstants.LEFT_TRIGGER),  false),
    DRIVER_RIGHT_TRIGGER ("Rotate Right", new WsJSJoystickInputConfig(0, JoystickConstants.RIGHT_TRIGGER), false),

    // ---------------------------------
    // Manipulator Joysticks
    // ---------------------------------
    MANIPULATOR_LEFT_JOYSTICK_Y  ("Manipulator left joystick y",  new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_Y),  false),
    MANIPULATOR_LEFT_JOYSTICK_X  ("Manipulator left joystick x",  new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_JOYSTICK_X),  false),
    MANIPULATOR_RIGHT_JOYSTICK_Y ("Manipulator right joystick y", new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_Y), false),
    MANIPULATOR_RIGHT_JOYSTICK_X ("Manipulator right joystick x", new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_JOYSTICK_X), false),

    // ---------------------------------
    // Manipulator DPAD Buttons
    // ---------------------------------
    MANIPULATOR_DPAD_DOWN  ("Manipulator dpad down",  new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_Y_DOWN),  false),
    MANIPULATOR_DPAD_LEFT  ("Manipulator dpad left",  new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_X_LEFT),  false),
    MANIPULATOR_DPAD_RIGHT ("Manipulator dpad right", new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_X_RIGHT), false),
    MANIPULATOR_DPAD_UP    ("Manipulator dpad up",    new WsDPadButtonInputConfig(1, JoystickConstants.DPAD_Y_UP),    false),

    // ---------------------------------
    // Manipulator Buttons
    // ---------------------------------
    MANIPULATOR_FACE_DOWN             ("Manipulator face down",             new WsJSButtonInputConfig(1, 0), false),
    MANIPULATOR_FACE_LEFT             ("High fuel down",                    new WsJSButtonInputConfig(1, 2), false),
    MANIPULATOR_FACE_RIGHT            ("High fuel up",                      new WsJSButtonInputConfig(1, 1), false),
    MANIPULATOR_FACE_UP               ("Manipulator face up",               new WsJSButtonInputConfig(1, 3), false),
    MANIPULATOR_LEFT_SHOULDER         ("Outtake down",                      new WsJSButtonInputConfig(1, 4), false),
    MANIPULATOR_RIGHT_SHOULDER        ("Outtake up",                        new WsJSButtonInputConfig(1, 5), false),
    MANIPULATOR_SELECT                ("Manipulator select",                new WsJSButtonInputConfig(1, 6), false),
    MANIPULATOR_START                 ("Manipulator start",                 new WsJSButtonInputConfig(1, 7), false),
    MANIPULATOR_LEFT_JOYSTICK_BUTTON  ("Manipulator left joystick button",  new WsJSButtonInputConfig(1, 8), false),
    MANIPULATOR_RIGHT_JOYSTICK_BUTTON ("Manipulator right joystick button", new WsJSButtonInputConfig(1, 9), false),

    // ---------------------------------
    // Manipulator Triggers
    // ---------------------------------
    MANIPULATOR_LEFT_TRIGGER  ("Intake Reverse",            new WsJSJoystickInputConfig(1, JoystickConstants.LEFT_TRIGGER),  false),
    MANIPULATOR_RIGHT_TRIGGER ("Intake",                    new WsJSJoystickInputConfig(1, JoystickConstants.RIGHT_TRIGGER), false),

    // ********************************
    // Digital IOs
    // ********************************
    
    // -------------------------------
    // Networked sensors
    // -------------------------------
    
    // ********************************
    // Others ...
    // ********************************
    //GYRO                    ("Gyro", new WsAnalogGyroConfig(0, true),         false),
            
    ; // end of enum
    
    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private final String m_name;
    private InputConfig m_config = null;
    private boolean m_trackingState;

    /**
     * Initialize a new Input.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for InputType.
     * @param p_trackingState True if the StateTracker should track this Input.
     */
    WSInputs(String p_name, InputConfig p_config, boolean p_trackingState) {
        m_name = p_name;
        m_config = p_config;
        m_trackingState = p_trackingState;
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
     * Returns true if the Logger should track the Input's state.
     * @return True if the StateTracker should track this Input.
     */
    public boolean isTrackingState() {
        return m_trackingState;
    }

}
