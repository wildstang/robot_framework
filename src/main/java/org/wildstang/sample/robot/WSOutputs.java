package org.wildstang.sample.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsServoConfig;
import org.wildstang.hardware.roborio.outputs.config.WsPhoenixConfig;
import org.wildstang.hardware.roborio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;
import org.wildstang.hardware.roborio.outputs.config.WsDigitalOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkMaxConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkMaxFollowerConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSolenoidConfig;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

/**
 * Output mappings are stored here.
 * Below each Motor, PWM, Digital Output, Solenoid, and Relay is enumerated with their appropriated IDs.
 * The enumeration includes a name, output type, and output config object.
 */
public enum WSOutputs implements Outputs {

    // ********************************
    // PWM Outputs
    // ********************************
    // ---------------------------------
    // Motors
    // ---------------------------------
    LEFT_DRIVE("Left Drive Motor", new WsSparkMaxConfig(CANConstants.LEFT_DRIVE, true)),
    RIGHT_DRIVE("Right Drive Motor", new WsSparkMaxConfig(CANConstants.RIGHT_DRIVE, true)),
    LEFT_DRIVE_FOLLOWER("Left Drive Follower", new WsSparkMaxFollowerConfig("Left Drive Motor", CANConstants.LEFT_DRIVE_FOLLOWER, true)),
    RIGHT_DRIVE_FOLLOWER("Right Drive Follower", new WsSparkMaxFollowerConfig("Right Drive Motor", CANConstants.RIGHT_DRIVE_FOLLOWER, true)),
    TEST_MOTOR("Test Motor", new WsPhoenixConfig(CANConstants.EXAMPLE_MOTOR_CONTROLLER, WsMotorControllers.VICTOR_SPX)),

    DRIVE1("Module 1 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE1, true)),
    ANGLE1("Module 1 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE1, true)),
    DRIVE2("Module 2 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE2, true)),
    ANGLE2("Module 2 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE2, true)),
    DRIVE3("Module 3 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE3, true)),
    ANGLE3("Module 3 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE3, true)),
    DRIVE4("Module 4 Drive Motor", new WsSparkMaxConfig(CANConstants.DRIVE4, true)),
    ANGLE4("Module 4 Angle Motor", new WsSparkMaxConfig(CANConstants.ANGLE4, true)),
    

    // ---------------------------------
    // Servos
    // ---------------------------------
    TEST_SERVO("Test Servo", new WsServoConfig(0, 0)),

    // ********************************
    // DIO Outputs
    // ********************************
    DIO_O_0("Test Digital Output 0", new WsDigitalOutputConfig(0, true)), // Channel 0, Initially Low

    // ********************************
    // Solenoids
    // ********************************

    TEST_SOLENOID("Test Solenoid", new WsSolenoidConfig(PneumaticsModuleType.REVPH, 0, false)),
    
    // ********************************
    // Relays
    // ********************************

    // ********************************
    // Others ...
    // ********************************
    LED("LEDs", new WsI2COutputConfig(I2C.Port.kMXP, 0x10));

    ; // end of enum

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private String m_name;
    private OutputConfig m_config;

    /**
     * Initialize a new Output.
     * @param p_name Name, must match that in class to prevent errors.
     * @param p_config Corresponding configuration for OutputType.
     */
    WSOutputs(String p_name, OutputConfig p_config) {
        m_name = p_name;
        m_config = p_config;
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

}