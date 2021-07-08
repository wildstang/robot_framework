package org.wildstang.sample.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsPhoenixConfig;

/**
 * Output mappings are stored here.
 * Below each Motor, PWM, Digital Output, Solenoid, and Relay is enumerated with their appropriated IDs.
 * The enumeration includes a name, output type, output config object, and tracking boolean.
 */
public enum WSOutputs implements Outputs {

    // ********************************
    // PWM Outputs
    // ********************************
    // ---------------------------------
    // Motors
    // ---------------------------------
    DRIVE_LEFT_MOTOR("Left Drive Motor", new WsPhoenixConfig(CANConstants.DRIVE_MOTOR_CONTROLLERS[0], 0, true, false), false),
    DRIVE_RIGHT_MOTOR("Right Drive Motor", new WsPhoenixConfig(CANConstants.DRIVE_MOTOR_CONTROLLERS[1], 0, true, true), false),
    OUTTAKE_MOTOR("Outtake Motor", new WsPhoenixConfig(CANConstants.OUTTAKE_MOTOR_CONTROLLER, 0, false), false),
    HIGHFUEL_MOTOR("High Fuel Motor", new WsPhoenixConfig(CANConstants.HIGHFUEL_MOTOR_CONTROLLER, 0, false), false),
    INTAKE_MOTOR("Intake Motor", new WsPhoenixConfig(CANConstants.INTAKE_MOTOR_CONTROLLER, 0, false, true), false),

    // ---------------------------------
    // Servos
    // ---------------------------------

    // ********************************
    // DIO Outputs
    // ********************************

    // ********************************
    // Solenoids
    // ********************************
    
    // ********************************
    // Relays
    // ********************************

    // ********************************
    // Others ...
    // ********************************
    //LED("LEDs", WSOutputType.I2C, new WsI2COutputConfig(I2C.Port.kMXP, 0x10), false);

    ; // end of enum

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private String m_name;
    private OutputConfig m_config;
    private boolean m_trackingState;

    /**
     * Initialize a new Output.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for OutputType.
     * @param p_trackingState True if the StateTracker should track this Output.
     */
    WSOutputs(String p_name, OutputConfig p_config, boolean p_trackingState) {
        m_name = p_name;
        m_config = p_config;
        m_trackingState = p_trackingState;
    }

    /**
     * Returns the name mapped to the Output.
     * @return Name mapped to the Output.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the config of Output for the enumeration.
     * @return OutputConfig of enumeration.
     */
    public OutputConfig getConfig() {
        return m_config;
    }

    /**
     * Returns true if the Logger should track the Output's state.
     * @return True if the StateTracker should track this Output.
     */
    public boolean isTrackingState() {
        return m_trackingState;
    }

}