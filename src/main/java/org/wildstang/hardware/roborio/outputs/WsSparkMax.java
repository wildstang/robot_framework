package org.wildstang.hardware.roborio.outputs;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.wildstang.framework.io.outputs.AnalogOutput;

/**
 * Controls a Spark Max motor controller.
 * @author Liam
 */
public class WsSparkMax extends AnalogOutput {

    CANSparkMax motor;
    
    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param p_default Default output value.
     */
    public WsSparkMax(String name, int channel, boolean brushless, double p_default) {
        this(name, channel, brushless, p_default, false);
    }

    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param p_default Default output value.
     * @param invert Invert the motor's direction.
     */
    public WsSparkMax(String name, int channel, boolean brushless, double p_default, boolean invert) {
        super(name, p_default);

        motor = new CANSparkMax(channel, brushless ? MotorType.kBrushless : MotorType.kBrushed);
        motor.setInverted(invert);
    }

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public void setBrake() {
        motor.setIdleMode(IdleMode.kBrake);
    }

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public void setCoast() {
        motor.setIdleMode(IdleMode.kCoast);
    }

    /**
     * Sets the current limit of the motor controller.
     * @param stallLimitAmps The amount of amps drawn before limiting while less than limitRPM.
     * @param freeLimitAmps The amount of amps drawn before limiting while greater than limitRPM.
     * @param limitRPM Sets the line between stallLimitAmps and freeLimitAmps.
     */
    public void setCurrentLimit(int stallLimitAmps, int freeLimitAmps, int limitRPM) {
        motor.setSmartCurrentLimit(stallLimitAmps, freeLimitAmps, limitRPM);
        motor.burnFlash();
    }

    /**
     * Add a follower motor to the current motor.
     * @param canConstant CAN constant of the new follower motor.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public void addFollower(int canConstant, boolean brushless, boolean oppose) {
        CANSparkMax follower = new CANSparkMax(canConstant, brushless ? MotorType.kBrushless : MotorType.kBrushed);
        follower.follow(motor, oppose);
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
     * Sets motor speed to current value, from -1.0 to 1.0.
     */
    @Override
    public void sendDataToOutput() {
        motor.set(getValue());
    }

    /**
     * Wraps setValue().
     * @param value New motor percent speed, from -1.0 to 1.0.
     */
    public void setSpeed(double value) {
        setValue(value);
    }

    /**
     * Sets the motors to 0 speed.
     */
    public void stop() {
        setSpeed(0);
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }
}
