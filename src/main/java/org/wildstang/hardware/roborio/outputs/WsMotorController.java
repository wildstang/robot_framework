package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;

/**
 * Abstract class decribing those that control some kind of motor controller.
 * @author Liam
 */
public abstract class WsMotorController extends AnalogOutput {

    /**
     * Generic motor controller constructor, cannot be called.
     * @param p_name Descriptive name of the controller.
     */
    public WsMotorController(String p_name) {
        super(p_name);
    }

    /**
     * Generic motor controller constructor, cannot be called.
     * @param p_name Descriptive name of the controller.
     * @param p_default Default output value.
     */
    public WsMotorController(String p_name, double p_default) {
        super(p_name, p_default);
    }

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public abstract void setBrake();

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public abstract void setCoast();

    /**
     * Returns the quadrature velocity from an encoder.
     * @return Current velocity.
     */
    public abstract double getVelocity();

    /**
     * Returns the quadrature position from an encoder.
     * @return Current position.
     */
    public abstract double getPosition();

    /**
     * Returns the current motor output percent.
     * @return Current motor output as a percent.
     */
    public abstract double getOutput();

    /**
     * Return the motor controller or motor temperature
     * @return Current temperature.
     */
    public abstract double getTemperature();
    
    /**
     * Sets motor speed to current value, from -1.0 to 1.0.
     */
    public abstract void sendDataToOutput();

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
}