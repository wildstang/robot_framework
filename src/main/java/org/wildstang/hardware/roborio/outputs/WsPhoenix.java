package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.logger.Log;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;

import java.util.ArrayList;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
 * Controls a Talon or Victor motor controller as a Phoenix BaseMotorController.
 * @author Liam
 */
public class WsPhoenix extends WsMotorController {

    BaseMotorController motor;
    BaseMotorController follower;

    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param p_default Default output value.
     * @param controller Enumeration representing type of controller.
     * @param invert Invert the motor's direction.
     */
    public WsPhoenix(String name, int channel, double p_default, WsMotorControllers controller, boolean invert) {
        super(name, p_default);

        switch (controller) {
            case TALON_SRX:
                motor = new TalonSRX(channel);
                break;
            case VICTOR_SPX:
                motor = new VictorSPX(channel);
                break;
            case TALON_FX:
                motor = new TalonFX(channel);
                break;
            default:
                Log.error("Invalid motor controller for WsPhoenix!");
                return;
        }
        motor.setInverted(invert);
    }

    /**
     * Add a follower motor to the current motor.
     * @param canConstant CAN constant of the new follower motor.
     * @param controller Enumeration representing type of controller.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public void addFollower(int canConstant, WsMotorControllers controller, boolean oppose) {
        switch (controller) {
            case TALON_SRX:
                follower = new TalonSRX(canConstant);
                break;
            case VICTOR_SPX:
                follower = new VictorSPX(canConstant);
                break;
            case TALON_FX:
                follower = new TalonFX(canConstant);
                break;
            default:
                Log.error("Invalid follower motor controller for WsPhoenix!");
                return;
        }
        follower.follow(motor);
        follower.setInverted(oppose ? InvertType.OpposeMaster : InvertType.FollowMaster);
    }

    /**
     * Returns the raw motor controller Object.
     * @return CANSparkMax Object.
     */
    public BaseMotorController getController() {
        return motor;
    }

    /**
     * Returns the raw follower motor controller Object.
     * @return Follower motor controller object, null if no follower.
     */
    public BaseMotorController getFollower() {
        return follower;
    }

    /**
     * Determines the type of motor controller represented.
     * @return Enumeration representing type of motor controller.
     */
    public WsMotorControllers getControllerType() {
        if (motor instanceof TalonSRX) {
            return WsMotorControllers.TALON_SRX;
        }
        else if (motor instanceof TalonFX) {
            return WsMotorControllers.TALON_FX;
        }
        else if (motor instanceof VictorSPX) {
            return WsMotorControllers.VICTOR_SPX;
        }
        return WsMotorControllers.UNKNOWN;
    }

    /**
     * Brake Functions
     */

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
     * Current Limit Functions
     */

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
        else if (motor instanceof TalonFX) {
            ((TalonFX) motor).configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, continuousLimitAmps, peakLimitAmps, peakDuractionMs));
        }
    }

    /**
     * Disables the current limit of the motor controller.
     * Only Talons support current limiting.
     */
    public void disableCurrentLimit() {
        if (motor instanceof TalonSRX) {
            ((TalonSRX) motor).enableCurrentLimit(false);
        }
        else if (motor instanceof TalonFX) {
            StatorCurrentLimitConfiguration config = new StatorCurrentLimitConfiguration();
            ((TalonFX) motor).configGetStatorCurrentLimit(config);
            config.enable = false;
        }
    }

    /**
     * Encoder Functions
     */

    /**
     * Returns the quadrature velocity from a Talon's encoder.
     * @return Current velocity, 0 if not a Talon.
     */
    public double getVelocity() {
        if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().getQuadratureVelocity();
        }
        else if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getSensorCollection().getIntegratedSensorVelocity();
        }
        return 0;
    }

    /**
     * Returns the quadrature position from a Talon's encoder.
     * @return Current position, 0 if not a Talon.
     */
    public double getPosition() {
        if (motor instanceof TalonSRX) {
            return ((TalonSRX) motor).getSensorCollection().getQuadraturePosition();
        }
        else if (motor instanceof TalonFX) {
            return ((TalonFX) motor).getSensorCollection().getIntegratedSensorPosition();
        }
        return 0;
    }
    
    /**
     * Resets the position of a Talon's encoder.
     */
    public void resetEncoder() {
        if (motor instanceof TalonSRX) {
            ((TalonSRX) motor).setSelectedSensorPosition(0, 0, -1);
        }
        else if (motor instanceof TalonFX) {
            ((TalonFX) motor).getSensorCollection().setIntegratedSensorPosition(0, -1);
        }
    }

    /**
     * Returns the state of a Talon's limit switches.
     * @return Returns 1 if the forward limit switch is closed, 0 if neither or both,
     * and -1 if the reverse limit switch is closed. 
     */
    public int limitSwitchState() {
        int state = 0;
        if (motor instanceof BaseTalon) {
            BaseTalon t = (BaseTalon) motor;
            // BaseTalon functions return 0 if open 1 if closed
            state = t.isFwdLimitSwitchClosed() - t.isRevLimitSwitchClosed();
        }
        return state;
    }

    /**
     * Motion Profile Functions
     */
    
    /**
     * Sets and runs the motion profile slot to use.
     * @param slot Motion profile slot number.
     */
    public void setProfile(int slot) {
        motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);
        motor.selectProfileSlot(slot, 0);
    }
    
    /**
     * Enables or disables motion profile.
     * @param enable True if to enable profile.
     */
    public void enableProfile(boolean enable) {
        if (!enable) {
            motor.clearMotionProfileTrajectories();
        }
        motor.set(ControlMode.MotionProfile,
            (enable ? SetValueMotionProfile.Enable : SetValueMotionProfile.Disable).value);
    }
    
    /**
     * Get the motor controller's motion profile status.
     * @return Status of the motion profile.
     */
    public MotionProfileStatus getProfileStatus() {
        MotionProfileStatus status = new MotionProfileStatus();
        motor.getMotionProfileStatus(status);
        return status;
    }

    /**
     * Fills the motor controllers path buffers with the upcoming points.
     * @param points List of upcoming trajectories.
     */
    public void fillProfile(ArrayList<TrajectoryPoint> points) {
        if (getProfileStatus().hasUnderrun) {
            motor.clearMotionProfileHasUnderrun();
        }

        /*
         * just in case we are interrupting another MP and there is still buffer points
         * in memory, clear it.
         */
        motor.clearMotionProfileTrajectories();

        /* This is fast since it's just into our TOP buffer */
        for (int i = 0; i < points.size(); ++i) {
            motor.pushMotionProfileTrajectory(points.get(i));
        }
    }

    /**
     * Processes the current motion profile's buffer.
     */
    public void updateProfile() {
        motor.processMotionProfileBuffer();
    }

    /**
     * Sets the motion profiles control frame period.
     * @param period Time in milliseconds.
     */
    public void setMotionControlFramePeriod(int period) {
        motor.changeMotionControlFramePeriod(20);
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
     * Output Functions
     */

    /**
     * Returns the current motor output percent.
     * @return Current motor output as a percent.
     */
    public double getOutput() {
        return motor.getMotorOutputPercent();
    }

    /**
     * Return the motor controller temperature
     */
    @Override
    public double getTemperature() {
        return motor.getTemperature();
    }

    /**
     * Sets motor speed to current value, from -1.0 to 1.0.
     */
    @Override
    public void sendDataToOutput() {
        motor.set(ControlMode.PercentOutput, getValue());
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() {}
}
