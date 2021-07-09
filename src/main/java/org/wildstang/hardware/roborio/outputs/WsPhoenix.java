package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
 * Controls a Talon or Victor motor controller as a Phoenix BaseMotorController.
 * @author Liam
 */
public class WsPhoenix extends AnalogOutput {

    BaseMotorController motor;
    
    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param p_default Default output value.
     * @param talon True if Talon, false if Victor.
     */
    public WsPhoenix(String name, int channel, double p_default, boolean talon) {
        this(name, channel, p_default, talon, false);
    }

    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param p_default Default output value.
     * @param talon True if Talon, false if Victor.
     * @param invert Invert the motor's direction.
     */
    public WsPhoenix(String name, int channel, double p_default, boolean talon, boolean invert) {
        super(name, p_default);

        if (talon) {
            motor = new TalonSRX(channel);
        }
        else {
            motor = new VictorSPX(channel);
        }
        motor.setInverted(invert);
    }

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public void setBrake() {
        motor.setNeutralMode(NeutralMode.Brake);
    }

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public void setCoast() {
        motor.setNeutralMode(NeutralMode.Coast);
    }

    /**
     * Sets the current limit of the motor controller.
     * Only Talons support current limiting.
     * @param peakLimitAmps The instantaneous amount of amps drawn before limiting.
     * @param peakDuractionMs The amount of time peakLimitAmps must be broken before limiting, in milliseconds.
     * @param continuousLimitAmps The continuous amount of amps drawn before limiting, normally less than peakLimitAmps.
     */
    public void setCurrentLimit(int peakLimitAmps, int peakDuractionMs, int continuousLimitAmps) {
        if (motor instanceof TalonSRX) {
            TalonSRX t = (TalonSRX) motor;
            t.configPeakCurrentLimit(peakLimitAmps);
            t.configPeakCurrentDuration(peakDuractionMs);
            t.configContinuousCurrentLimit(continuousLimitAmps);
            t.enableCurrentLimit(true);
        }
    }

    /**
     * Add a follower motor to the current motor.
     * @param canConstant CAN constant of the new follower motor.
     * @param talon True if Talon, false if Victor.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public void addFollower(int canConstant, boolean talon, boolean oppose) {
        BaseMotorController follower;
        if (talon) {
            follower = new TalonSRX(canConstant);
        }
        else {
            follower = new VictorSPX(canConstant);
        }
        follower.follow(motor);
        follower.setInverted(oppose ? InvertType.OpposeMaster : InvertType.FollowMaster);
    }

    /**
     * Disables the current limit of the motor controller.
     * Only Talons support current limiting.
     */
    public void disableCurrentLimit() {
        if (motor instanceof TalonSRX) {
            ((TalonSRX) motor).enableCurrentLimit(false);
        }
    }

    /**
     * Returns the quadrature velocity from a Talon's encoder.
     * @return Current velocity, 0 if not a Talon.
     */
    public int getVelocity() {
        if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().getQuadratureVelocity();
        }
        return 0;
    }

    /**
     * Returns the quadrature position from a Talon's encoder.
     * @return Current position, 0 if not a Talon.
     */
    public int getPosition() {
        if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().getQuadraturePosition();
        }
        return 0;
    }
    
    /**
     * Sets and runs the motion profile slot to use.
     * @param slot Motion profile slot number.
     */
    public void runProfile(int slot) {
        motor.set(ControlMode.MotionProfile, 0);
        motor.selectProfileSlot(slot, 0);
    }

    /**
     * Actively holds a given position, useful for braking.
     * @param slot Motion profile slot number.
     */
    public void holdPosition(int slot) {
        motor.selectProfileSlot(slot, 0);
        motor.set(ControlMode.Position, motor.getSelectedSensorPosition());
    }
    
    /**
     * Resets the position of a Talon's encoder.
     */
    public void resetEncoder() {
        if (motor instanceof TalonSRX) {
            ((TalonSRX) motor).setSelectedSensorPosition(0, 0, -1);
        }
    }

    /**
     * Returns the current motor output percent.
     * @return Current motor output as a percent.
     */
    public double getOutput() {
        return motor.getMotorOutputPercent();
    }

    /**
     * Sets motor speed to current value, from -1.0 to 1.0.
     */
    @Override
    public void sendDataToOutput() {
        motor.set(ControlMode.PercentOutput, getValue());
    }

    /**
     * Sets the motor value and applys it to the controller.
     * @param value New motor percent speed, from -1.0 to 1.0.
     */
    public void setSpeed(double value) {
        setValue(value);
        sendDataToOutput();
    }

    /**
     * Sets the motors to 0 speed and enables brake mode.
     */
    public void stop() {
        setSpeed(0);
        setBrake();
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }
}
