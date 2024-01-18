package org.wildstang.sample.robot;

// expand this and edit if trouble with Ws
import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.io.outputs.Output;
import org.wildstang.hardware.roborio.outputs.config.WsServoConfig;
import org.wildstang.hardware.roborio.outputs.config.WsPhoenixConfig;
import org.wildstang.hardware.roborio.outputs.config.WsRemoteAnalogOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;
import org.wildstang.hardware.roborio.outputs.config.WsDigitalOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkFollowerConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSolenoidConfig;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

/**
 * Output mappings are stored here.
 * Below each Motor, PWM, Digital Output, Solenoid, and Relay is enumerated with their appropriated IDs.
 * The enumeration includes a name, output type, and output config object.
 */
public enum WsOutputs implements Outputs {

    // ********************************
    // PWM Outputs
    // ********************************
    // ---------------------------------
    // Motors
    // ---------------------------------
    //rename this String, and it shouldn't be a follower, but a normal motor

    DRIVE1("Module 1 Drive Motor", new WsSparkConfig(CANConstants.DRIVE1, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE1("Module 1 Angle Motor", new WsSparkConfig(CANConstants.ANGLE1, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE2("Module 2 Drive Motor", new WsSparkConfig(CANConstants.DRIVE2, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE2("Module 2 Angle Motor", new WsSparkConfig(CANConstants.ANGLE2, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE3("Module 3 Drive Motor", new WsSparkConfig(CANConstants.DRIVE3, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE3("Module 3 Angle Motor", new WsSparkConfig(CANConstants.ANGLE3, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    DRIVE4("Module 4 Drive Motor", new WsSparkConfig(CANConstants.DRIVE4, WsMotorControllers.SPARK_FLEX_BRUSHLESS)),
    ANGLE4("Module 4 Angle Motor", new WsSparkConfig(CANConstants.ANGLE4, WsMotorControllers.SPARK_MAX_BRUSHLESS)),
    
    // ---------------------------------
    // Servos
    // ---------------------------------
    //TEST_SERVO("Test Servo", new WsServoConfig(0, 0)),

    // ********************************
    // DIO Outputs
    // ********************************
    //DIO_O_0("Test Digital Output 0", new WsDigitalOutputConfig(0, true)), // Channel 0, Initially Low

    // ********************************
    // Solenoids
    // ********************************
    //TEST_SOLENOID("Test Solenoid", new WsSolenoidConfig(PneumaticsModuleType.REVPH, 0, false)),
    
    // ********************************
    // Relays
    // ********************************

    // ********************************
    // NetworkTables
    // ********************************
    // LL_MODE("camMode", new WsRemoteAnalogOutputConfig("limelight", 0)),
    // LL_LEDS("ledMode", new WsRemoteAnalogOutputConfig("limelight", 0)),

    // ********************************
    // Others ...
    // ********************************

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
    WsOutputs(String p_name, OutputConfig p_config) {
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

    /**
     * Returns the actual Output object from the OutputManager
     * @return The corresponding output.
     */
    public Output get() {
        return Core.getOutputManager().getOutput(this);
    }
}