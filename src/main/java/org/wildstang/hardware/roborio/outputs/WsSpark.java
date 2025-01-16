package org.wildstang.hardware.roborio.outputs;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.AbsoluteEncoderConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import org.wildstang.framework.logger.Log;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;

/**
 * Controls a Spark Max/Flex motor controller.
 * @author Liam
 */
public class WsSpark extends WsMotorController {

    SparkBase motor;
    SparkBase follower;
    SparkBaseConfig config;
    SparkBaseConfig followerConfig;
    AbsoluteEncoderConfig absEncoderConfig = new AbsoluteEncoderConfig();
    boolean isUsingController;
    boolean isChanged;
    com.revrobotics.spark.SparkBase.ControlType controlType;
    
    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param p_default Default output value.
     */
    public WsSpark(String name, int channel, WsMotorControllers controller, double p_default) {
        this(name, channel, controller, p_default, false);
    }

    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param p_default Default output value.
     * @param invert Invert the motor's direction.
     */
    public WsSpark(String name, int channel, WsMotorControllers controller, double p_default, boolean invert) {
        super(name, p_default);

        boolean brushless = controller == WsMotorControllers.SPARK_MAX_BRUSHLESS || controller == WsMotorControllers.SPARK_FLEX_BRUSHLESS;
        switch (controller) {
            case SPARK_MAX_BRUSHED:
            case SPARK_MAX_BRUSHLESS:
                motor = new SparkMax(channel, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                config = new SparkMaxConfig();
                break;
            case SPARK_FLEX_BRUSHED:
            case SPARK_FLEX_BRUSHLESS:
                motor = new SparkFlex(channel, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                config = new SparkFlexConfig();
                break;
            default:
                Log.error("Invalid motor controller for WsSpark!");
                return;
        }
        config.inverted(invert);
        isUsingController = false;
        isChanged = true;
        controlType = ControlType.kDutyCycle;
    }

    /**
     * Add a follower motor to the current motor.
     * @param canConstant CAN constant of the new follower motor.
     * @param controller Enumeration representing type of controller.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public void addFollower(int canConstant, WsMotorControllers controller, boolean oppose) {
        boolean brushless = controller == WsMotorControllers.SPARK_MAX_BRUSHLESS || controller == WsMotorControllers.SPARK_FLEX_BRUSHLESS;
        switch (controller) {
            case SPARK_MAX_BRUSHED:
            case SPARK_MAX_BRUSHLESS:
                follower = new SparkMax(canConstant, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                followerConfig = new SparkMaxConfig();
                break;
            case SPARK_FLEX_BRUSHED:
            case SPARK_FLEX_BRUSHLESS:
                follower = new SparkFlex(canConstant, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                followerConfig = new SparkFlexConfig();
                break;
            default:
                Log.error("Invalid follower motor controller for WsSpark!");
                return;
        }
        followerConfig.follow(motor, oppose);
    }

    /**
     * Returns the raw motor controller Object.
     * @return SparkBase Object.
     */
    public SparkBase getController() {
        return motor;
    }

    /**
     * Returns the raw follower motor controller Object.
     * @return Follower motor controller object, null if no follower.
     */
    public SparkBase getFollower() {
        return follower;
    }

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public void setBrake() {
        config.idleMode(IdleMode.kBrake);
        if (follower != null)
        {
            followerConfig.idleMode(IdleMode.kBrake);
        }
    }

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public void setCoast() {
        config.idleMode(IdleMode.kCoast);
        if (follower != null)
        {
            followerConfig.idleMode(IdleMode.kCoast);
        }
    }

    /**
     * Sets the current limit of the motor controller.
     * @param stallLimitAmps The amount of amps drawn before limiting while less than limitRPM.
     * @param freeLimitAmps The amount of amps drawn before limiting while greater than limitRPM.
     * @param limitRPM Sets the line between stallLimitAmps and freeLimitAmps.
     */
    public void setCurrentLimit(int stallLimitAmps, int freeLimitAmps, int limitRPM) {
        config.smartCurrentLimit(stallLimitAmps, freeLimitAmps, limitRPM);
        if (follower != null){
            followerConfig.smartCurrentLimit(stallLimitAmps, freeLimitAmps, limitRPM);
        }
        enableVoltageCompensation();
        configure();
    }

    /*
     * Burn to flash the current config files
     */
    public void configure(){
        motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        if (follower != null){
            follower.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        }
    }

    /**
     * Sets the current limit, but does not burn flash. Never use in init
     * @param limit the amount of amps drawn before limiting
     */
    public void tempCurrentLimit(int limit){
        config.smartCurrentLimit(limit,limit,0);
        if (follower != null){
            followerConfig.smartCurrentLimit(limit, limit, 0);
        }
    }

    /**
     * Enables voltage compensation.
     */
    public void enableVoltageCompensation(){
        config.voltageCompensation(12);
        if (follower != null){
            followerConfig.voltageCompensation(12);
        }
    }

    /**
     * Returns the quadrature velocity from an encoder.
     * @return Current velocity.
     */
    public double getVelocity() {
        return motor.getEncoder().getVelocity();
    }

    /**
     * Returns the quadrature position from an encoder.
     * @return Current position.
     */
    public double getPosition() {
        return motor.getEncoder().getPosition();
    }
    
    /**
     * Resets the position of an encoder.
     */
    public void resetEncoder() {
        motor.getEncoder().setPosition(0.0);
    }

    /**
     * Returns the current motor output percent.
     * @return Current motor output as a percent.
     */
    public double getOutput() {
        return motor.getAppliedOutput();
    }

    /**
     * Returns the temperature of the motor.
     */
    @Override
    public double getTemperature() {
        return motor.getMotorTemperature();
    }

    /**
     * Sets motor control
     */
    @Override
    public void sendDataToOutput() {
        if (isChanged){
            if (!isUsingController){
                motor.set(getValue());
            } else {
                if (controlType == ControlType.kPosition){
                    motor.getClosedLoopController().setReference(super.getValue(), ControlType.kPosition);
                } else if (controlType == ControlType.kVelocity){
                    motor.getClosedLoopController().setReference(super.getValue(), ControlType.kVelocity);
                } else if (controlType == ControlType.kDutyCycle){
                    motor.getClosedLoopController().setReference(super.getValue(), ControlType.kDutyCycle);
                }
            }
        }
    }

    /**
     * Wraps setValue().
     * @param value New motor percent speed, from -1.0 to 1.0.
     */
    @Override
    public void setSpeed(double value){
        if (controlType == ControlType.kDutyCycle && super.getValue()==value){
            isChanged = false;
        } else {
            isChanged = true;
        }
        controlType = ControlType.kDutyCycle;
        super.setSpeed(value);
    }

    /**
     * Sets up closed loop control for the motor
     * @param P the P value
     * @param I the I value
     * @param D the D value
     * @param FF the feed forward constant
     */
    public void initClosedLoop(double P, double I, double D, double FF){
        config.closedLoop.pidf(P, I, D, FF);
        config.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
        isUsingController = true;
    }

    /**
     * Sets up closed loop control for the motor
     * @param P the P value
     * @param I the I value
     * @param D the D value
     * @param FF the feed forward constant
     * @param absEncoder absolute encoder used to provided PID feedback
     */
    public void initClosedLoop(double P, double I, double D, double FF, boolean isEncoderFlipped){
        config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot0);
        absEncoderConfig.positionConversionFactor(360.0);
        absEncoderConfig.velocityConversionFactor(360.0/60.0);
        absEncoderConfig.inverted(isEncoderFlipped);
        config.apply(absEncoderConfig);
        config.closedLoop.feedbackSensor(FeedbackSensor.kAbsoluteEncoder);
        config.closedLoop.positionWrappingEnabled(true);
        config.closedLoop.positionWrappingMaxInput(360.0);
        config.closedLoop.positionWrappingMinInput(0.0);
        isUsingController = true;
    }
    /**
     * Sets up closed loop control for the motor
     * @param P the P value
     * @param I the I value
     * @param D the D value
     * @param FF the feed forward constant
     * @param absEncoder absolute encoder used to provided PID feedback
     * @param isWrapped whether wrapping should be enabled
     */
    public void initClosedLoop(double P, double I, double D, double FF, boolean isEncoderFlipped, boolean isWrapped){
        config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot0);
        absEncoderConfig.positionConversionFactor(360.0);
        absEncoderConfig.velocityConversionFactor(360.0/60.0);
        absEncoderConfig.inverted(isEncoderFlipped);
        config.apply(absEncoderConfig);
        config.closedLoop.feedbackSensor(FeedbackSensor.kAbsoluteEncoder);
        config.closedLoop.positionWrappingEnabled(isWrapped);
        config.closedLoop.positionWrappingMaxInput(360.0);
        config.closedLoop.positionWrappingMinInput(0.0);
        isUsingController = true;
        isUsingController = true;
    }

    /*
     * Adds a closed loop control slot for the sparkmax
     * @param slotID the slot number of the constants, 0 is default, either 1-3 otherwise
     * @param PIDFF the constants values
     */
    public void addClosedLoop(int slotID, double P, double I, double D, double FF){
        if (slotID == 0) config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot0);
        else if (slotID == 1) config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot1);
        else if (slotID == 2) config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot2);
        else config.closedLoop.pidf(P, I, D, FF, ClosedLoopSlot.kSlot3);
    }

    /**
     * Sets the motor to track the given position
     * @param target the encoder target value to track to
     */
    public void setPosition(double target){
        if (super.getValue() == target && controlType == ControlType.kPosition){
            isChanged = false;
        } else{
            isChanged = true;
        }
        super.setValue(target);
        controlType = ControlType.kPosition;
    }

    /**
     * Sets the motor to track the given position with a specific PIDFF constants
     * @param target the encoder target to track to
     * @param slotID the ID slot of the sparkmax to use
     */
    public void setPosition(double target, int slotID){
        if (super.getValue() == target && controlType == ControlType.kPosition){
            isChanged = false;
        } else{
            isChanged = true;
        }
        super.setValue(target);
        controlType = ControlType.kPosition;
    }

    /**
     * Sets the motor to track the given velocity
     * @param target the encoder target value velocity to track to
     */
    public void setVelocity(double target){
        if (super.getValue() == target && controlType == ControlType.kVelocity){
            isChanged = false;
        } else{
            isChanged = true;
        }
        super.setValue(target);
        controlType = ControlType.kVelocity;
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }

    /**
     * Does nothing, Spark has no current limit disable function.
     */
    @Override
    public void disableCurrentLimit() {}
}
