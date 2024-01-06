package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix.sensors.Pigeon2;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.sample.robot.CANConstants;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsOutputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsVision;
import org.wildstang.sample.subsystems.targeting.LimeConsts;
import org.wildstang.hardware.roborio.outputs.WsSparkMax;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Class: SwerveDrive
 * inputs: driver left joystick x/y, right joystick x, right trigger, right bumper, select, face buttons all, gyro
 * outputs: four swerveModule objects
 * description: controls a swerve drive for four swerveModules through autonomous and teleoperated control
 */
public class SwerveDrive extends SwerveDriveTemplate {

    private AnalogInput leftStickX;//translation joystick x
    private AnalogInput leftStickY;//translation joystick y
    private AnalogInput rightStickX;//rot joystick
    private AnalogInput rightTrigger;//thrust
    private AnalogInput leftTrigger;//scoring autodrive
    // private DigitalInput rightBumper;//robot centric control
    private DigitalInput leftBumper;//hp station pickup
    private DigitalInput select;//gyro reset
    private DigitalInput start;//snake mode
    private DigitalInput faceUp;//rotation lock 0 degrees
    private DigitalInput faceRight;//rotation lock 90 degrees
    private DigitalInput faceLeft;//rotation lock 270 degrees
    private DigitalInput faceDown;//rotation lock 180 degrees
    private DigitalInput dpadLeft;//defense mode
    private DigitalInput rightStickButton;//auto drive override

    private double xSpeed;
    private double ySpeed;
    private double rotSpeed;
    private double thrustValue;
    private boolean rotLocked;
    private boolean isSnake;
    private boolean isFieldCentric;
    private double rotTarget;
    private double pathVel;
    private double pathHeading;
    private double pathAccel;
    private double pathTarget;
    private double aimOffset;
    private double vertOffset;
    private double pathXOffset = 0;
    private double pathYOffset = 0;
    private boolean autoOverride;
    private boolean isBlue;
    private boolean autoTag = false;
    
    private final double mToIn = 39.37;

    //private final AHRS gyro = new AHRS(SerialPort.Port.kUSB);
    private final Pigeon2 gyro = new Pigeon2(CANConstants.GYRO);
    public SwerveModule[] modules;
    private SwerveSignal swerveSignal;
    private WsSwerveHelper swerveHelper = new WsSwerveHelper();
    private SwerveDriveOdometry odometry;
    private Timer autoTimer = new Timer();

    private WsVision limelight;
    private LimeConsts LC;

    public enum driveType {TELEOP, AUTO, CROSS};
    public driveType driveState;

    @Override
    public void inputUpdate(Input source) {

        //determine if we are in cross or teleop
        if (driveState != driveType.AUTO && dpadLeft.getValue()) {
            driveState = driveType.CROSS;
            for (int i = 0; i < modules.length; i++) {
                modules[i].setDriveBrake(true);
            }
            this.swerveSignal = new SwerveSignal(new double[]{0, 0, 0, 0 }, swerveHelper.setCross().getAngles());
        }
        else if (driveState == driveType.CROSS || driveState == driveType.AUTO) {
            driveState = driveType.TELEOP;
        }

        //get x and y speeds
        xSpeed = swerveHelper.scaleDeadband(leftStickX.getValue(), DriveConstants.DEADBAND);
        ySpeed = swerveHelper.scaleDeadband(leftStickY.getValue(), DriveConstants.DEADBAND);
        
        //reset gyro
        if (source == select && select.getValue()) {
            gyro.setYaw(0.0);
            if (rotLocked) rotTarget = 0.0;
        }

        //determine snake or pid locks
        if (start.getValue() && (Math.abs(xSpeed) > 0.1 || Math.abs(ySpeed) > 0.1)) {
            rotLocked = true;
            isSnake = true;
            rotTarget = swerveHelper.getDirection(xSpeed, ySpeed);
        }
        else {
            isSnake = false;
        }
        if ((source == faceUp && faceUp.getValue()) || leftBumper.getValue()){
            if (faceLeft.getValue() || faceRight.getValue()){ rotTarget = getClosestRotation();
            } else  rotTarget = 0.0;
            rotLocked = true;
        }
        if (source == faceLeft && faceLeft.getValue()){
            if (faceUp.getValue() || faceDown.getValue()){ rotTarget = getClosestRotation();
            } else rotTarget = 270.0;
            rotLocked = true;
        }
        if ((source == faceDown && faceDown.getValue() && !leftBumper.getValue()) || Math.abs(leftTrigger.getValue()) > 0.15){
            if (faceLeft.getValue() || faceRight.getValue()){ rotTarget = getClosestRotation();
            } else rotTarget = 180.0;
            rotLocked = true;
        }
        if (source == faceRight && faceRight.getValue()){
            if (faceUp.getValue() || faceDown.getValue()){ rotTarget = getClosestRotation();
            } else rotTarget = 90.0;
            rotLocked = true;
        }

        //get rotational joystick
        rotSpeed = rightStickX.getValue()*Math.abs(rightStickX.getValue());
        rotSpeed = swerveHelper.scaleDeadband(rotSpeed, DriveConstants.DEADBAND);
        if (Math.abs(leftTrigger.getValue()) > 0.15) rotSpeed = 0;
        rotSpeed *=1.5;
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) {
            rotLocked = false;
        }
        
        //assign thrust
        thrustValue = 1 - DriveConstants.DRIVE_THRUST + DriveConstants.DRIVE_THRUST * Math.abs(rightTrigger.getValue());
        xSpeed *= thrustValue;
        ySpeed *= thrustValue;
        rotSpeed *= thrustValue;

    }
 
    @Override
    public void init() {
        initInputs();
        initOutputs();
        resetState();
        gyro.setYaw(0.0);
    }

    public void initInputs() {
        LC = new LimeConsts();

        leftStickX = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_JOYSTICK_X);
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_JOYSTICK_Y);
        leftStickY.addInputListener(this);
        rightStickX = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_JOYSTICK_X);
        rightStickX.addInputListener(this);
        rightTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_RIGHT_TRIGGER);
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_TRIGGER);
        leftTrigger.addInputListener(this);
        // rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER);
        // rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_LEFT_SHOULDER);
        leftBumper.addInputListener(this);
        select = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_SELECT);
        select.addInputListener(this);
        start = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_START);
        start.addInputListener(this);
        faceUp = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_FACE_UP);
        faceUp.addInputListener(this);
        faceLeft = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_FACE_LEFT);
        faceLeft.addInputListener(this);
        faceRight = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_FACE_RIGHT);
        faceRight.addInputListener(this);
        faceDown = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_FACE_DOWN);
        faceDown.addInputListener(this);
        dpadLeft = (DigitalInput) Core.getInputManager().getInput(WsInputs.DRIVER_DPAD_LEFT);
        dpadLeft.addInputListener(this);
        rightStickButton = (DigitalInput) WsInputs.DRIVER_RIGHT_JOYSTICK_BUTTON.get();
        rightStickButton.addInputListener(this);
    }

    public void initOutputs() {
        //create four swerve modules
        modules = new SwerveModule[]{
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.DRIVE1), 
                (WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.ANGLE1), DriveConstants.FRONT_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.DRIVE2), 
                (WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.ANGLE2), DriveConstants.FRONT_RIGHT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.DRIVE3), 
                (WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.ANGLE3), DriveConstants.REAR_LEFT_OFFSET),
            new SwerveModule((WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.DRIVE4), 
                (WsSparkMax) Core.getOutputManager().getOutput(WsOutputs.ANGLE4), DriveConstants.REAR_RIGHT_OFFSET)
        };
        //create default swerveSignal
        swerveSignal = new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0, 0.0});
        limelight = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);
        odometry = new SwerveDriveOdometry(new SwerveDriveKinematics(new Translation2d(0.2794, 0.2794), new Translation2d(0.2794, -0.2794),
            new Translation2d(-0.2794, 0.2794), new Translation2d(-0.2794, -0.2794)), odoAngle(), odoPosition(), new Pose2d());
    }
    
    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        odometry.update(odoAngle(), odoPosition());

        if (driveState == driveType.CROSS) {
            //set to cross - done in inputupdate
            this.swerveSignal = swerveHelper.setCross();
            drive();
        }
        if (driveState == driveType.TELEOP) {
            if (rotLocked){
                //if rotation tracking, replace rotational joystick value with controller generated one
                rotSpeed = swerveHelper.getRotControl(rotTarget, getGyroAngle());
                if (isSnake) {
                    if (Math.abs(rotSpeed) < 0.05) {
                        rotSpeed = 0;
                    }
                    else {
                        rotSpeed *= 4;
                        if (Math.abs(rotSpeed) > 1) rotSpeed = 1.0 * Math.signum(rotSpeed);
                    }
                    
                } 
            }
            this.swerveSignal = swerveHelper.setDrive(xSpeed, ySpeed, rotSpeed, getGyroAngle());
            SmartDashboard.putNumber("FR signal", swerveSignal.getSpeed(0));
            drive();
        }
        if (driveState == driveType.AUTO) {
            //get controller generated rotation value
            rotSpeed = Math.max(-0.2, Math.min(0.2, swerveHelper.getRotControl(pathTarget, getGyroAngle())));
            //ensure rotation is never more than 0.2 to prevent normalization of translation from occuring
            if (autoTag){
                xSpeed = limelight.getScoreX(aimOffset);
                ySpeed = limelight.getScoreY(vertOffset);
                if (Math.abs(xSpeed) > 0.3) xSpeed = Math.signum(xSpeed) * 0.3;
                if (Math.abs(ySpeed) > 0.3) ySpeed = Math.signum(ySpeed) * 0.3; 
                if (Math.abs(pathVel * DriveConstants.DRIVE_F_V) > Math.abs(ySpeed*0.5)){
                    ySpeed = 0.0;//no adjustment when coming towards tag
                } else {
                    pathVel = 0.0;//adjustment when close enough to tag
                }
                pathXOffset = 0;//disables odometry tracking
                pathYOffset = 0;
            } else {
                xSpeed = 0;//no LL adjustments if tag is off
                ySpeed = 0;
            }
            
            //update where the robot is, to determine error in path
            this.swerveSignal = swerveHelper.setAuto(swerveHelper.getAutoPower(pathVel, pathAccel), pathHeading, rotSpeed,getGyroAngle(),pathXOffset+xSpeed, pathYOffset+ySpeed);
            drive();        
        } 
        SmartDashboard.putNumber("Gyro Reading", getGyroAngle());
        SmartDashboard.putNumber("X speed", xSpeed);
        SmartDashboard.putNumber("Y speed", ySpeed);
        SmartDashboard.putNumber("rotSpeed", rotSpeed);
        SmartDashboard.putString("Drive mode", driveState.toString());
        SmartDashboard.putBoolean("rotLocked", rotLocked);
        SmartDashboard.putNumber("Auto velocity", pathVel);
        SmartDashboard.putNumber("Auto translate direction", pathHeading);
        SmartDashboard.putNumber("Auto rotation target", pathTarget);
    }
    
    @Override
    public void resetState() {
        xSpeed = 0;
        ySpeed = 0;
        rotSpeed = 0;
        setToTeleop();
        rotLocked = false;
        rotTarget = 0.0;
        pathVel = 0.0;
        pathHeading = 0.0;
        pathAccel = 0.0;
        pathTarget = 0.0;
        aimOffset = 0.0;
        vertOffset = 0.0;
        autoOverride = false;
        autoTag = false;

        isFieldCentric = true;
        isSnake = false;
    }

    @Override
    public String getName() {
        return "Swerve Drive";
    }

    /** resets the drive encoders on each module */
    public void resetDriveEncoders() {
        for (int i = 0; i < modules.length; i++) {
            modules[i].resetDriveEncoders();
        }
    }

    /** sets the drive to teleop/cross, and sets drive motors to coast */
    public void setToTeleop() {
        driveState = driveType.TELEOP;
        for (int i = 0; i < modules.length; i++) {
            modules[i].setDriveBrake(true);
        }
        rotSpeed = 0;
        xSpeed = 0;
        ySpeed = 0;
        pathHeading = 0;
        pathVel = 0;
        pathAccel = 0;
        rotLocked = false;
    }

    /**sets the drive to autonomous */
    public void setToAuto() {
        driveState = driveType.AUTO;
        for (int i = 0; i < modules.length; i++) {
            modules[i].setDriveBrake(true);
        }
    }

    /**drives the robot at the current swerveSignal, and displays information for each swerve module */
    private void drive() {
        if (driveState == driveType.CROSS) {
            for (int i = 0; i < modules.length; i++) {
                modules[i].runCross(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
        else {
            for (int i = 0; i < modules.length; i++) {
                modules[i].run(swerveSignal.getSpeed(i), swerveSignal.getAngle(i));
                modules[i].displayNumbers(DriveConstants.POD_NAMES[i]);
            }
        }
    }

    /**sets autonomous values from the path data file */
    public void setAutoValues(double velocity, double heading, double accel, double xOffset, double yOffset) {
        pathVel = velocity;
        pathHeading = heading;
        pathAccel = accel;
        pathXOffset = xOffset;
        pathYOffset = yOffset;
    }

    /**sets the autonomous heading controller to a new target */
    public void setAutoHeading(double headingTarget) {
        pathTarget = headingTarget;
    }

    /**
     * Resets the gyro, and sets it the input number of degrees
     * Used for starting the match at a non-0 angle
     * @param degrees the current value the gyro should read
     */
    public void setGyro(double degrees) {
        resetState();
        setToAuto();
        gyro.setYaw(degrees);
    }

    public double getGyroAngle() {
        if (!isFieldCentric) return 0;
        //limelight.setGyroValue((gyro.getYaw() + 360)%360);
        return (359.99 - gyro.getYaw()+360)%360;
    }  
    public Rotation2d odoAngle(){
        return new Rotation2d(Math.toRadians(360-getGyroAngle()));
    }
    public SwerveModulePosition[] odoPosition(){
        return new SwerveModulePosition[]{modules[0].odoPosition(), modules[1].odoPosition(), modules[2].odoPosition(), modules[3].odoPosition()};
    }
    public void setOdo(Pose2d starting){
        this.odometry.resetPosition(odoAngle(), odoPosition(), starting);
        autoTimer.start();
    }
    public Pose2d returnPose(double returnVelocity){
        return odometry.getPoseMeters();
    }
    public double getRotTarget(){
        return rotTarget;
    }
    public void setAutoTag(boolean isOn, boolean isBlue){
        autoTag = isOn;
        this.isBlue = isBlue;
    }
    private double getClosestRotation(){
        if (getGyroAngle() < 90.0) return 45.0;
        if (getGyroAngle() < 180.0) return 135.0;
        if (getGyroAngle() < 270.0) return 225.0;
        return 315.0;
    }
}
