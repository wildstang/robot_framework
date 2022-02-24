package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix.sensors.CANCoder;
import com.kauailabs.navx.frc.AHRS;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;
import org.wildstang.sample.robot.CANConstants;
import org.wildstang.sample.robot.WSInputs;
import org.wildstang.sample.robot.WSOutputs;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Class: SwerveDrive
 * inputs: driver left joystick x/y, right joystick x, right trigger, right bumper, select, face buttons all, gyro
 * outputs: four swerveModule objects
 * description: controls a swerve drive for four swerveModules through autonomous and teleoperated control
 */
public class SwerveDrive extends SwerveDriveTemplate {

    private final SlewRateLimiter xSpeedLimiter = new SlewRateLimiter(DriveConstants.SLEW_RATE_LIMIT);
    private final SlewRateLimiter ySpeedLimiter = new SlewRateLimiter(DriveConstants.SLEW_RATE_LIMIT);
    private final SlewRateLimiter rotSpeedLimiter = new SlewRateLimiter(DriveConstants.SLEW_RATE_LIMIT);

    private AnalogInput leftStickX;//translation joystick x
    private AnalogInput leftStickY;//translation joystick y
    private AnalogInput rightStickX;//rot joystick
    private AnalogInput rightTrigger;//thrust
    private DigitalInput rightBumper;//defense mode, aka cross
    private DigitalInput select;//gyro reset
    private DigitalInput faceUp;//rotation lock 0 degrees
    private DigitalInput faceRight;//rotation lock 90 degrees
    private DigitalInput faceLeft;//rotation lock 270 degrees
    private DigitalInput faceDown;//rotation lock 180 degrees

    private double xSpeed;
    private double ySpeed;
    private double rotSpeed;
    private boolean isFieldOriented;
    private double thrustValue;
    private boolean rotLocked;
    private double rotTarget;
    private double pathPos;
    private double pathVel;
    private double pathHeading;
    private double pathTarget;
    private double autoTravelled;
    private double autoTempX;
    private double autoTempY;

    private final AHRS gyro = new AHRS(I2C.Port.kOnboard);
    private SwerveModule[] modules;
    private SwerveSignal swerveSignal;
    private WSSwerveHelper swerveHelper = new WSSwerveHelper();

    public enum driveType {TELEOP, AUTO, CROSS};
    public driveType driveState;

    @Override
    public void inputUpdate(Input source) {
        //determine if we are in cross or teleop
        if (driveState != driveType.AUTO && rightBumper.getValue()){
            driveState = driveType.CROSS;
        } else if (driveState != driveType.AUTO){
            driveState = driveType.TELEOP;
        }
        //get x and y speeds
        xSpeed = -xSpeedLimiter.calculate(leftStickY.getValue());
        if (Math.abs(leftStickY.getValue()) < DriveConstants.DEADBAND) xSpeed = 0;
        ySpeed = ySpeedLimiter.calculate(leftStickX.getValue());
        if (Math.abs(leftStickX.getValue()) < DriveConstants.DEADBAND) ySpeed = 0;
        
        if (source == select && select.getValue()) gyro.reset();
        thrustValue = 1 - DriveConstants.DRIVE_THRUST + DriveConstants.DRIVE_THRUST * Math.abs(rightTrigger.getValue());

        //update auto trackign values
        if (faceUp.getValue()){
            rotTarget = 0.0;
            rotLocked = true;
        } else if (faceLeft.getValue()){
            rotTarget = 270.0;
            rotLocked = true;
        } else if (faceRight.getValue()){
            rotTarget = 90.0;
            rotLocked = true;
        } else if (faceDown.getValue()){
            rotTarget = 180.0;
            rotLocked = true;
        }
        //get rotational joystick
        rotSpeed = -rotSpeedLimiter.calculate(rightStickX.getValue());
        if (Math.abs(rightStickX.getValue()) < DriveConstants.DEADBAND) rotSpeed = 0;
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) rotLocked = false;
    }
 
    @Override
    public void init() {
        // TODO Auto-generated method stub
        initInputs();
        initOutputs();
        resetState();
        gyro.reset();
    }

    public void initInputs(){
        leftStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X);
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        leftStickY.addInputListener(this);
        rightStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_JOYSTICK_X);
        rightStickX.addInputListener(this);
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        select = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_SELECT);
        select.addInputListener(this);
        faceUp = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_UP);
        faceUp.addInputListener(this);
        faceLeft = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_LEFT);
        faceLeft.addInputListener(this);
        faceRight = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_RIGHT);
        faceRight.addInputListener(this);
        faceDown = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_FACE_DOWN);
        faceDown.addInputListener(this);
    }

    public void initOutputs(){
        //create four swerve modules
        modules = new SwerveModule[]{
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE1), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE1), new CANCoder(CANConstants.ENC1), DriveConstants.FRONT_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE2), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE2), new CANCoder(CANConstants.ENC2), DriveConstants.FRONT_RIGHT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE3), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE3), new CANCoder(CANConstants.ENC3), DriveConstants.REAR_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.DRIVE4), 
                (WsSparkMax) Core.getOutputManager().getOutput(WSOutputs.ANGLE4), new CANCoder(CANConstants.ENC4), DriveConstants.REAR_RIGHT_OFFSET)
        };
        //create default swerveSignal
        swerveSignal = new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0, 0.0});
    }
    
    @Override
    public void selfTest() {
        // TODO Auto-generated method stub
    }

    @Override
    public void update() {
        switch (driveState) {
        case CROSS:
            //if not translating, then set to cross
            if (xSpeed == 0 && ySpeed == 0){
                swerveSignal = swerveHelper.setCross();
                drive();
            } else {
                //if translating, set to crab
                swerveSignal = swerveHelper.setCrab(xSpeed, ySpeed, gyro.getAngle());
                drive();
            }
        case TELEOP:
            if (rotLocked){
                //if rotation tracking, replace rotational joystick value with controller generated one
                rotSpeed = swerveHelper.getRotControl(rotTarget, gyro.getAngle());
            }
            swerveSignal = swerveHelper.setDrive(xSpeed, ySpeed, rotSpeed, gyro.getAngle());
            drive();
        case AUTO:
            //get controller generated rotation value
            rotSpeed = swerveHelper.getRotControl(pathTarget, gyro.getAngle());
            //ensure rotation is never more than 0.2 to prevent normalization of translation from occuring
            if (Math.abs(rotSpeed) > 0.2) rotSpeed /= (Math.abs(rotSpeed * 5));
            //update where the robot is, to determine error in path
            updateAutoDistance();
            swerveSignal = swerveHelper.setAuto(swerveHelper.getAutoPower(pathPos, pathVel, autoTravelled), pathHeading, rotSpeed, gyro.getAngle());
            drive();

        
        }
        SmartDashboard.putNumber("Gyro Reading", gyro.getRotation2d().getDegrees());
        SmartDashboard.putBoolean("Is field oriented", isFieldOriented);
        SmartDashboard.putNumber("Thrust value", thrustValue);
    }

    @Override
    public void resetState() {
        xSpeed = 0;
        ySpeed = 0;
        rotSpeed = 0;
        isFieldOriented = true;//should be true
        //gyro.reset();
        setToTeleop();
        rotLocked = false;
        rotTarget = 0.0;
        pathPos = 0.0;
        pathVel = 0.0;
        pathHeading = 0.0;
        pathTarget = 0.0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Swerve Drive";
    }
    /** resets the drive encoders on each module */
    public void resetDriveEncoders(){
        for (int i = 0; i < modules.length; i++){
            modules[i].resetDriveEncoders();
        }
        autoTravelled = 0.0;
    }
    /** sets the drive to teleop/cross, and sets drive motors to coast */
    public void setToTeleop(){
        driveState = driveType.TELEOP;
        for (int i = 0; i < modules.length; i++){
            modules[i].setDriveBrake(false);
        }
    }
    /**sets the drive to autonomous */
    public void setToAuto(){
        driveState = driveType.AUTO;
    }
    /**stops the robot from moving */
    public void stopMoving(){
        swerveSignal = swerveHelper.setCrab(0.0, 0.0, gyro.getAngle());
        drive();
    }
    /**drives the robot at the current swerveSignal, and displays information for each swerve module */
    private void drive(){
        for (int i = 0; i < modules.length; i++){
            modules[i].run(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
            modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
        }
    }
    /**sets autonomous values from the path data file */
    public void setAutoValues(double position, double velocity, double heading){
        pathPos = position;
        pathVel = velocity;
        pathHeading = heading;
    }
    /**sets the autonomous heading controller to a new target */
    public void setAutoHeading(double headingTarget){
        pathTarget = headingTarget;
    }
    /**updates distance travelled in autonomous, to determine error in path following */
    private void updateAutoDistance(){
        for (int i = 0; i < modules.length; i++){
            autoTempX += modules[i].getPosition() * Math.cos(Math.toRadians(modules[i].getAngle()));
            autoTempY += modules[i].getPosition() * Math.sin(Math.toRadians(modules[i].getAngle()));
        }
        autoTravelled += Math.hypot(autoTempX/modules.length, autoTempY/modules.length);
    }
    
}
