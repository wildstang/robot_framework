package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.logger.Log;
import org.wildstang.hardware.roborio.outputs.config.WsMotorControllers;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;


/**
 * Controls a Talon or Victor motor controller as a Phoenix BaseMotorController.
 * @author Liam
 */
public class WsTalon extends WsMotorController {

    private CurrentLimitsConfigs limitConfigs = new CurrentLimitsConfigs();
    private TalonFXConfiguration config = new TalonFXConfiguration();
    private MotorOutputConfigs motorConfig = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);
    private TalonFX motor;
    private TalonFXConfigurator motorApply;
    private Follower follow;
    private TalonFX follower;
    private TalonFXConfigurator followerApply;

    private enum RequestType {PERCENT_OUTPUT, POSITION}
    private RequestType currentRequest = RequestType.PERCENT_OUTPUT;
    private DutyCycleOut percentRequest = new DutyCycleOut(0.0);
    private PositionDutyCycle positionRequest = new PositionDutyCycle(0.0);
    

    private boolean followerExists = false;

    /**
     * Constructs the motor controller from config.
     * @param name Descriptive name of the controller.
     * @param channel Motor controller CAN constant.
     * @param p_default Default output value.
     */
    public WsTalon(String name, int channel, String CANBUS, double p_default) {
        super(name, p_default);
        motor = new TalonFX(channel, CANBUS);
        motorApply = motor.getConfigurator();
        //percentRequest.withEnableFOC(true);
    }

    /**
     * Add a follower motor to the current motor.
     * @param canConstant CAN constant of the new follower motor.
     * @param CANBUS Attached CAN bus name.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public void addFollower(int canConstant, String CANBUS, boolean oppose) {
        follower = new TalonFX(canConstant, CANBUS);
        followerApply = follower.getConfigurator();
        followerExists = true;
        MotorAlignmentValue alignment = oppose ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned;
        follow = new Follower(motor.getDeviceID(), alignment);
    }


    /**
     * Brake Functions
     */

    /**
     * Sets the motor to brake mode, will not freely spin.
     */
    public void setBrake() {
        motorConfig.withNeutralMode(NeutralModeValue.Brake);
        config.withMotorOutput(motorConfig);
        applyConfigs();
    }

    /**
     * Sets the motor to coast mode, will freely spin.
     */
    public void setCoast() {
        motorConfig.withNeutralMode(NeutralModeValue.Coast);
        config.withMotorOutput(motorConfig);
        applyConfigs();
    }

    /**
     * Current Limit Functions
     */

    /**
     * Sets the current limit of the motor controller.
     * @param statorLimit the stator limit to apply
     * @param supplyLimit the supply limit to apply
     */
    public void setCurrentLimit(int statorLimit, int supplyLimit) {
        limitConfigs.StatorCurrentLimit = statorLimit;
        limitConfigs.StatorCurrentLimitEnable = true;
        limitConfigs.SupplyCurrentLimit = supplyLimit;
        limitConfigs.SupplyCurrentLimitEnable = true;
        config.withCurrentLimits(limitConfigs);
        applyConfigs();
    }
    public void setCurrentLimit(int statorLimit){
        limitConfigs.StatorCurrentLimit = statorLimit;
        limitConfigs.StatorCurrentLimitEnable = true;
        limitConfigs.SupplyCurrentLimitEnable = false;
        config.withCurrentLimits(limitConfigs);
        applyConfigs();
    }


    /**
     * Encoder Functions
     */

    /**
     * Returns the quadrature velocity from a Talon's encoder.
     * @return Current velocity, 0 if not a Talon.
     */
    public double getVelocity() {
        return motor.getVelocity().getValueAsDouble();
    }

    /**
     * Returns the quadrature position from a Talon's encoder.
     * @return Current position, 0 if not a Talon.
     */
    public double getPosition() {
        return motor.getPosition().getValueAsDouble();
    }

    public double getOutput(){
        return motor.getDutyCycle().getValueAsDouble();
    }

    public double getTemperature(){
        return motor.getDeviceTemp().getValueAsDouble();
    }

    /**
     * Sets motor speed to current value, from -1.0 to 1.0.
     */
    @Override
    public void sendDataToOutput() {
        if (currentRequest == RequestType.PERCENT_OUTPUT){
            motor.setControl(percentRequest);
        } else if (currentRequest == RequestType.POSITION){
            motor.setControl(positionRequest);
        }
        if (followerExists) follower.setControl(follow);
    }

    public void setSpeed(double value){
        percentRequest.withOutput(value);
        currentRequest = RequestType.PERCENT_OUTPUT;
    }
    
    /**
     * Sets up closed loop control for the motor
     * @param P the P value
     * @param I the I value
     * @param D the D value
     */
    public void initClosedLoop(double P, double I, double D){
        Slot0Configs slot0 = new Slot0Configs();
        slot0.kP = P;
        slot0.kI = I;
        slot0.kD = D;
        motorApply.apply(slot0);
    }

    /*
     * Adds a closed loop control slot for the sparkmax
     * @param slotID the slot number of the constants, 0 is default, either 1-3 otherwise
     * @param PIDFF the constants values
     */
    public void addClosedLoop(double P, double I, double D){
        Slot1Configs slotNew = new Slot1Configs();
        slotNew.kP = P;
        slotNew.kI = I;
        slotNew.kD = D;
        motorApply.apply(slotNew);
    }

    /**
     * Sets the motor to track the given position
     * @param target the encoder target value to track to
     */
    public void setPosition(double target){
        positionRequest.withPosition(target);
        currentRequest = RequestType.POSITION;
    }

    /**
     * Sets the motor to track the given position with a specific PIDFF constants
     * @param target the encoder target to track to
     * @param slotID the ID slot of the sparkmax to use
     */
    public void setPosition(double target, int slotID){
        positionRequest.withSlot(slotID);
        positionRequest.withPosition(target);
        currentRequest = RequestType.POSITION;
    }


    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() {}

    private void applyConfigs(){
        motorApply.apply(config);
        if (followerExists){
            followerApply.apply(config);
        }
    }
}
