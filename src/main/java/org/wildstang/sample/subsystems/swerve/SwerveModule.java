package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {

    private double target;
    private double drivePower;

    private WsSparkMax driveMotor;
    private WsSparkMax angleMotor;
    private CANCoder canCoder;

    /** Class: SwerveModule
     *  controls a single swerve pod, featuring two motors and one offboard sensor
     * @param driveMotor canSparkMax of the drive motor
     * @param angleMotor canSparkMax of the angle motor
     * @param canCoder canCoder offboard encoder
     * @param offset double value of cancoder when module is facing forward
     */
    public SwerveModule(WsSparkMax driveMotor, WsSparkMax angleMotor, CANCoder canCoder, double offset){
        this.driveMotor = driveMotor;
        this.angleMotor = angleMotor;
        this.canCoder = canCoder;
        this.driveMotor.setCoast();
        this.angleMotor.setBrake();

        //set up angle and drive with pid and kpid respectively
        driveMotor.initClosedLoop(DriveConstants.DRIVE_P, DriveConstants.DRIVE_I, DriveConstants.DRIVE_D, DriveConstants.DRIVE_F);
        angleMotor.initClosedLoop(DriveConstants.ANGLE_P, DriveConstants.ANGLE_I, DriveConstants.ANGLE_D, 0);
        

        CANCoderConfiguration canCoderConfiguration = new CANCoderConfiguration();
        canCoderConfiguration.magnetOffsetDegrees = offset;
        canCoder.configAllSettings(canCoderConfiguration);

    }
    /** return double for cancoder position 
     * @return double for cancoder value (degrees)
    */
    public double getAngle(){
        return canCoder.getAbsolutePosition();
    }
    /** displays module information, needs the module name from super 
     * @param name the name of this module
    */
    public void displayNumbers(String name){
        SmartDashboard.putNumber(name + " CANCoder", canCoder.getAbsolutePosition());
        SmartDashboard.putNumber(name + " NEO angle encoder", angleMotor.getPosition());
        SmartDashboard.putNumber(name + " NEO angle target", target);
        SmartDashboard.putNumber(name + " NEO drive power", drivePower);
    }
    /** resets drive encoder */
    public void resetDriveEncoders(){
        driveMotor.setPosition(0.0);
    }
    /**sets drive to brake mode if true, coast if false 
     * @param isBrake true for brake, false for coast
    */
    public void setDriveBrake(boolean isBrake){
        if(isBrake){
            driveMotor.setBrake();
        } else {
            driveMotor.setCoast();
        }
    }
    /** runs module at double power [0,1] and robot centric bearing degrees angle 
     * @param power power [0, 1] to run the module at
     * @param angle angle to run the robot at, bearing degrees
    */
    public void run(double power, double angle){
        if (getDirection(angle)){
            runAtPower(power);
            runAtAngle(angle);
        } else {
            runAtPower(-power);
            runAtAngle((angle+180.0)%360);
        }
    }
    /**runs at specified robot centric bearing degrees angle 
     * @param angle angle to run the module at, bearing degrees
    */
    private void runAtAngle(double angle){
        double currentRotation = getAngle();

        if (currentRotation > 180 && angle+180<currentRotation){
            currentRotation-=360.0;
        } else if (angle>180 && currentRotation+180<angle){
            currentRotation+=360.0;
        }
        
        double deltaRotation = -currentRotation + angle;
        double deltaTicks = deltaRotation/360 * DriveConstants.TICKS_PER_REV * DriveConstants.ANGLE_RATIO;
        double currentTicks = angleMotor.getPosition();
        angleMotor.setPosition(currentTicks + deltaTicks);
    }
    /**runs module drive at specified power [-1, 1] 
     * @param power the power to run the module at, [-1, 1]
    */
    private void runAtPower(double power){
        driveMotor.setSpeed(power);
    }
    /** returns drive encoder distance in inches 
     * @return double drive encoder distance in inches
    */
    public double getPosition(){
        return driveMotor.getPosition() * DriveConstants.WHEEL_DIAMETER * Math.PI / DriveConstants.DRIVE_RATIO;
    }
    /**determines if it is faster to travel towards angle at positive power (true), or away from angle with negative power (false) 
     * @param angle the angle you are moving towards
     * @return boolean whether you should move towards that angle or the opposite
    */
    public boolean getDirection(double angle){
        if (Math.abs(angle - getAngle()) < 90 || Math.abs(angle - getAngle()) > 270) return true;
        return false;
    }
}
