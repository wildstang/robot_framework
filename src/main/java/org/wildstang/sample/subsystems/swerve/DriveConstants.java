package org.wildstang.sample.subsystems.swerve;

import static edu.wpi.first.units.Units.MetersPerSecond;

import org.wildstang.sample.robot.CANConstants;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue;
import com.ctre.phoenix6.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.swerve.SwerveModuleConstantsFactory;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.ClosedLoopOutputType;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.DriveMotorArrangement;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.SteerFeedbackType;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.SteerMotorArrangement;

import edu.wpi.first.units.measure.LinearVelocity;

public final class DriveConstants {
    /** How far is close enough to our target position (m)  */
    public static final double POSITION_TOLERANCE = 0.075;
    /**P Loop for Translating to a point */
    public static final double ALIGN_P = 0.32*2.0;
    /** D term for Translating to a point */
    public static final double ALIGN_D = 0;
    /**deadband of the controller's joysticks */
    public static final double DEADBAND = 0.05;
    /**PID values for driveF coefficient of momentum */
    public static final double DRIVE_F_V = 0.00375 * 1.20894*157.48/125.5*157.5/155.75*157.5/159*0.7 * 39.3701;//free speed of 22.2 ft/s becomes 266.4 in/s
    /**PID values for drive F coefficient of kinetic friction */
    public static final double DRIVE_F_K = 0.016;
    /**PID values for drive F coefficient of inertia */
    public static final double DRIVE_F_I = 0.000;//

    //heading PID value
    public static final double heading_P = 0.1;
    //heading PID value
    public static final double heading_D = 0.0;

    //steer PID values
    private static final Slot0Configs steerGains = new Slot0Configs()
        .withKP(100).withKI(0).withKD(0.5).withKS(0.1).withKV(1.91).withKA(0)
        .withStaticFeedforwardSign(StaticFeedforwardSignValue.UseClosedLoopSign);
    //drive PID values
    private static final Slot0Configs driveGains = new Slot0Configs()
        .withKP(0.1).withKI(0).withKD(0).withKS(0).withKV(0.124);
    private static final ClosedLoopOutputType steerClosedLoopOutput = ClosedLoopOutputType.Voltage;
    private static final ClosedLoopOutputType driveClosedLoopOutput = ClosedLoopOutputType.Voltage;
    private static final DriveMotorArrangement driveType = DriveMotorArrangement.TalonFX_Integrated;
    private static final SteerMotorArrangement steertype = SteerMotorArrangement.TalonFX_Integrated;
    private static final SteerFeedbackType steerEncoder = SteerFeedbackType.FusedCANcoder;

    //todo measure? from 2910
    private static final double slipCurrent = 120;
    //drive with 100A stator and 50A supply limits
    private static final TalonFXConfiguration driveInitialConfig = new TalonFXConfiguration()
        .withCurrentLimits(new CurrentLimitsConfigs().withStatorCurrentLimit(120).withStatorCurrentLimitEnable(true)
        .withSupplyCurrentLimit(70).withSupplyCurrentLimitEnable(true));
    //steer with 90A stator and 30A supply limits
    private static final TalonFXConfiguration steerInitialConfig = new TalonFXConfiguration()
        .withCurrentLimits(new CurrentLimitsConfigs().withStatorCurrentLimit(60).withStatorCurrentLimitEnable(true)
        .withSupplyCurrentLimit(30).withSupplyCurrentLimitEnable(true));
    private static final CANcoderConfiguration encoderInitialConfigs = new CANcoderConfiguration();
    private static final Pigeon2Configuration pigeonConfigs = null;
    //name of the can bus
    public static final CANBus canBus = new CANBus("drive_canivore", "./logs/example.hoot");
    
    //find this from swerve website
    public static final LinearVelocity maxSpeed = MetersPerSecond.of(4.54);
    //this is equal to module teeth divided by drive motor pinion teeth, 2910 had 54/12
    private static final double coupleRatio = 3.81818181818181813;
    //drive gear ratio
    private static final double driveRatio = 7.3636363636;
    //steer gear ratio
    private static final double steerRatio = 15.42857142857143;
    //empirically find this, 2910 had 1.95
    private static final double wheelRadius = 2.167;
    private static final boolean invertLeft = false;
    private static final boolean invertRight = true;
    //width to center of wheel
    private static final double halfWidth = 13;
    //length to center of wheel
    private static final double halfLength = 13;

    //bevel gear points to the left
    //find magnet offset
    private static final double FL_encoderOffset = 0.0;
    //find magnet offset
    private static final double FR_encoderOffset = 0.0;
    //find magnet offset
    private static final double BL_encoderOffset = 0.0;
    //find magnet offset
    private static final double BR_encoderOffset = 0.0;

    public static final SwerveDrivetrainConstants swerveCTREconsts = new SwerveDrivetrainConstants()
        .withCANBusName(canBus.getName()).
        withPigeon2Id(CANConstants.GYRO)
        .withPigeon2Configs(pigeonConfigs);
    private static final SwerveModuleConstantsFactory<TalonFXConfiguration, TalonFXConfiguration,CANcoderConfiguration> creator = 
        new SwerveModuleConstantsFactory<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration>()
            .withDriveMotorGearRatio(driveRatio)
            .withSteerMotorGearRatio(steerRatio)
            .withCouplingGearRatio(coupleRatio)
            .withWheelRadius(wheelRadius)
            .withSteerMotorGains(steerGains)
            .withDriveMotorGains(driveGains)
            .withSteerMotorClosedLoopOutput(steerClosedLoopOutput)
            .withDriveMotorClosedLoopOutput(driveClosedLoopOutput)
            .withSlipCurrent(slipCurrent)
            .withSpeedAt12Volts(maxSpeed)
            .withDriveFrictionVoltage(0.25)
            .withSteerFrictionVoltage(0.001)
            .withDriveInertia(0.001)
            .withSteerInertia(0.00001)
            .withDriveMotorType(driveType)
            .withSteerMotorType(steertype)
            .withFeedbackSource(steerEncoder)
            .withDriveMotorInitialConfigs(driveInitialConfig)
            .withSteerMotorInitialConfigs(steerInitialConfig)
            .withEncoderInitialConfigs(encoderInitialConfigs);
    
    public static final SwerveModuleConstants<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration> frontLeft = 
        creator.createModuleConstants(CANConstants.ANGLE1, CANConstants.DRIVE1, CANConstants.FL_ENCODER, FL_encoderOffset,
            halfWidth, halfLength, invertLeft, true, false);
    public static final SwerveModuleConstants<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration> frontRight = 
        creator.createModuleConstants(CANConstants.ANGLE2, CANConstants.DRIVE2, CANConstants.FR_ENCODER, FR_encoderOffset,
            halfWidth, -halfLength, invertRight, true, false);
    public static final SwerveModuleConstants<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration> backLeft = 
        creator.createModuleConstants(CANConstants.ANGLE3, CANConstants.DRIVE3, CANConstants.BL_ENCODER, BL_encoderOffset,
            -halfWidth, halfLength, invertLeft, true, false);
    public static final SwerveModuleConstants<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration> backRight = 
        creator.createModuleConstants(CANConstants.ANGLE4, CANConstants.DRIVE4, CANConstants.BR_ENCODER, BR_encoderOffset,
            -halfWidth, -halfLength, invertRight, true, false);
}


