package org.wildstang.hardware.roborio.outputs;

import com.revrobotics.SparkPIDController;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkBase.ControlType;

import org.wildstang.framework.logger.Log;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;

/**
 * Controls a Spark Max/Flex motor controller.
 * @author Liam
 */
public class WsSpark extends WsMotorController {

    CANSparkBase motor;
    CANSparkBase follower;
    SparkPIDController controller;
    boolean isUsingController;
    boolean isChanged;
    ControlType controlType;
    int slotID;
    
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
                motor = new CANSparkMax(channel, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                break;
            case SPARK_FLEX_BRUSHED:
            case SPARK_FLEX_BRUSHLESS:
                motor = new CANSparkFlex(channel, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                break;
            default:
                Log.error("Invalid motor controller for WsSpark!");
                return;
        }
        motor.setInverted(invert);
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
                follower = new CANSparkMax(canConstant, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                break;
            case SPARK_FLEX_BRUSHED:
            case SPARK_FLEX_BRUSHLESS:
                follower = new CANSparkFlex(canConstant, brushless ? MotorType.kBrushless : MotorType.kBrushed);
                break;
            default:
                Log.error("Invalid follower motor controller for WsSpark!");
                return;
        }
        follower.follow(motor, oppose);
    }

    /**
     * Returns the raw motor controller Object.
     * @return CANSparkBase Object.
     */
    public CANSparkBase getController() {
        return motor;
    }

    /**
     * Returns the raw follower motor controller Object.
     * @return Follower motor controller object, null if no follower.
     */
    public CANSparkBase getFollower() {
        return follower;
    }

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public void setBrake() {
        motor.setIdleMode(IdleMode.kBrake);
        if (follower != null)
        {
            follower.setIdleMode(IdleMode.kBrake);
        }
    }

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public void setCoast() {
        motor.setIdleMode(IdleMode.kCoast);
        if (follower != null)
        {
            follower.setIdleMode(IdleMode.kCoast);
        }
    }

    /**
     * Sets the current limit of the motor controller.
     * @param stallLimitAmps The amount of amps drawn before limiting while less than limitRPM.
     * @param freeLimitAmps The amount of amps drawn before limiting while greater than limitRPM.
     * @param limitRPM Sets the line between stallLimitAmps and freeLimitAmps.
     */
    public void setCurrentLimit(int stallLimitAmps, int freeLimitAmps, int limitRPM) {
        motor.setSmartCurrentLimit(stallLimitAmps, freeLimitAmps, limitRPM);
        enableVoltageCompensation();
        motor.burnFlash();
    }

    /**
     * Enables voltage compensation.
     */
    public void enableVoltageCompensation(){
        motor.enableVoltageCompensation(12);
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
                    controller.setReference(super.getValue(), controlType, slotID);
                } else if (controlType == ControlType.kVelocity){
                    controller.setReference(super.getValue(), controlType);
                } else if (controlType == ControlType.kDutyCycle){
                    controller.setReference(super.getValue(), controlType);
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
        controller = motor.getPIDController();
        controller.setP(P, 0);
        controller.setI(I, 0);
        controller.setD(D, 0);
        controller.setFF(FF, 0);
        isUsingController = true;
        slotID = 0;
    }

    /**
     * Sets up closed loop control for the motor
     * @param P the P value
     * @param I the I value
     * @param D the D value
     * @param FF the feed forward constant
     * @param absEncoder absolute encoder used to provided PID feedback
     */
    public void initClosedLoop(double P, double I, double D, double FF, AbsoluteEncoder absEncoder){
        controller = motor.getPIDController();
        controller.setP(P, 0);
        controller.setI(I, 0);
        controller.setD(D, 0);
        controller.setFF(FF, 0);
        controller.setFeedbackDevice(absEncoder);
        controller.setPositionPIDWrappingEnabled(true);
        controller.setPositionPIDWrappingMinInput(0.0);
        controller.setPositionPIDWrappingMaxInput(360.0);
        isUsingController = true;
        slotID = 0;
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
    public void initClosedLoop(double P, double I, double D, double FF, AbsoluteEncoder absEncoder, boolean isWrapped){
        controller = motor.getPIDController();
        controller.setP(P, 0);
        controller.setI(I, 0);
        controller.setD(D, 0);
        controller.setFF(FF, 0);
        controller.setFeedbackDevice(absEncoder);
        controller.setPositionPIDWrappingEnabled(isWrapped);
        controller.setPositionPIDWrappingMinInput(0.0);
        controller.setPositionPIDWrappingMaxInput(360.0);
        isUsingController = true;
        slotID = 0;
    }

    public SparkPIDController getPIDController(){
        return motor.getPIDController();
    }

    /*
     * Adds a closed loop control slot for the sparkmax
     * @param slotID the slot number of the constants, 0 is default, either 1-3 otherwise
     * @param PIDFF the constants values
     */
    public void addClosedLoop(int slotID, double P, double I, double D, double FF){
        controller.setP(P, slotID);
        controller.setI(I, slotID);
        controller.setD(D, slotID);
        controller.setFF(FF, slotID);

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
        slotID = 0;
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
        this.slotID = slotID;
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
