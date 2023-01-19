package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {

    private double target;
    private double encoderTarget;
    private double drivePower;
    private double chassisOffset;

    private WsSparkMax driveMotor;
    private WsSparkMax angleMotor;
    private AbsoluteEncoder absEncoder;

    /** Class: SwerveModule
     *  controls a single swerve pod, featuring two motors and one offboard sensor
     * @param driveMotor canSparkMax of the drive motor
     * @param angleMotor canSparkMax of the angle motor
     * @param canCoder canCoder offboard encoder
     * @param offset double value of cancoder when module is facing forward
     */
    public SwerveModule(WsSparkMax driveMotor, WsSparkMax angleMotor, double offset) {
        this.driveMotor = driveMotor;
        this.angleMotor = angleMotor;
        this.absEncoder = angleMotor.getController().getAbsoluteEncoder(Type.kDutyCycle);
        this.absEncoder.setInverted(true);
        this.absEncoder.setPositionConversionFactor(360.0);
        this.absEncoder.setVelocityConversionFactor(360.0/60.0);
        this.driveMotor.setCoast();
        this.angleMotor.setBrake();

        chassisOffset = offset;
        
        //set up angle and drive with pid and kpid respectively
        driveMotor.initClosedLoop(DriveConstants.DRIVE_P, DriveConstants.DRIVE_I, DriveConstants.DRIVE_D, 0);
        angleMotor.initClosedLoop(DriveConstants.ANGLE_P, DriveConstants.ANGLE_I, DriveConstants.ANGLE_D, 0, this.absEncoder);

        driveMotor.setCurrentLimit(DriveConstants.DRIVE_CURRENT_LIMIT, DriveConstants.DRIVE_CURRENT_LIMIT, 0);
        angleMotor.setCurrentLimit(DriveConstants.ANGLE_CURRENT_LIMIT, DriveConstants.ANGLE_CURRENT_LIMIT, 0);

    }

    /** return double for cancoder position 
     * @return double for cancoder value (degrees)
    */
    public double getAngle() {
        return ((359.99 - absEncoder.getPosition()+chassisOffset)+360)%360;
    }

    /** displays module information, needs the module name from super 
     * @param name the name of this module
    */
    public void displayNumbers(String name) {
        SmartDashboard.putNumber(name + " true angle", getAngle());
        SmartDashboard.putNumber(name + " raw angle", absEncoder.getPosition());
        SmartDashboard.putNumber(name + " true target", target);
        SmartDashboard.putNumber(name + " raw target", (359.99 - target + chassisOffset)%360);
        SmartDashboard.putNumber(name + " NEO drive power", drivePower);
        SmartDashboard.putNumber(name + " NEO drive position", driveMotor.getPosition());
    }

    /** resets drive encoder */
    public void resetDriveEncoders() {
        driveMotor.resetEncoder();
    }

    /**sets drive to brake mode if true, coast if false 
     * @param isBrake true for brake, false for coast
    */
    public void setDriveBrake(boolean isBrake) {
        if(isBrake) {
            driveMotor.setBrake();
        }
        else {
            driveMotor.setCoast();
        }
    }

    /** runs module at double power [0,1] and robot centric bearing degrees angle 
     * @param power power [0, 1] to run the module at
     * @param angle angle to run the robot at, bearing degrees
    */
    public void run(double power, double angle) {
        this.drivePower = power;
        this.target = angle;
        if (Math.abs(power) < 0.01) {
            runAtPower(power);
        }
        else if (getDirection(angle)) {
            runAtPower(power);
            runAtAngle(angle);
        }
        else {
            runAtPower(-power);
            runAtAngle((angle + 180.0) % 360);
        }
    }

    public void runCross(double position, double angle) {
        this.drivePower = position;
        this.target = angle;
        driveMotor.setSpeed(drivePower);
        if (getDirection(angle)) {
            runAtAngle(angle);
        }
        else {
            runAtAngle((angle + 180.0) % 360);
        }
    }

    /**runs at specified robot centric bearing degrees angle 
     * @param angle angle to run the module at, bearing degrees
    */
    private void runAtAngle(double angle) {
        angleMotor.setPosition(((359.99 - angle)+chassisOffset)%360);
    }

    /**runs module drive at specified power [-1, 1] 
     * @param power the power to run the module at, [-1, 1]
    */
    public void runAtPower(double power) {
        driveMotor.setSpeed(power);
    }

    /** returns drive encoder distance in inches 
     * @return double drive encoder distance in inches
    */
    public double getPosition() {
        return driveMotor.getPosition() * DriveConstants.WHEEL_DIAMETER * Math.PI / DriveConstants.DRIVE_RATIO;
    }

    /**returns raw drive encoder value, rotations
     * @return drive encoder value, rotations
     */
    public double getRawEncoderValue() {
        return driveMotor.getPosition();
    }

    /**determines if it is faster to travel towards angle at positive power (true), or away from angle with negative power (false) 
     * @param angle the angle you are moving towards
     * @return boolean whether you should move towards that angle or the opposite
    */
    public boolean getDirection(double angle) {
        return Math.abs(angle - getAngle()) < 90 || Math.abs(angle - getAngle()) > 270;
    } 

    public WsSparkMax getDriveMotor() {
        return driveMotor;
    }
}
