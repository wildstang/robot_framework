package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModule.SteerRequestType;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;
import org.wildstang.hardware.roborio.inputs.WsJoystickAxis;
import org.wildstang.hardware.roborio.inputs.WsJoystickButton;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.VisionConsts;
import org.wildstang.sample.subsystems.targeting.WsPose;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**Class: SwerveDrive
 * inputs: driver left joystick x/y, right joystick x, right trigger, right bumper, select, face buttons all, gyro
 * outputs: four swerveModule objects
 * description: controls a swerve drive for four swerveModules through autonomous and teleoperated control
 */
public class SwerveDrive extends SwerveDriveTemplate implements LoggableInputs {
    private AnalogInput leftStickHorizontal;//translation joystick x
    private AnalogInput leftStickVertical;//translation joystick y
    private AnalogInput rightStickHorizontal;//rot joystick
    private AnalogInput rightTrigger;//intake, score when scoring sequence
    private AnalogInput leftTrigger;//scoring sequence
    private DigitalInput rightBumper;//prestaged algae intake
    private DigitalInput leftBumper;//hp station pickup
    private DigitalInput select;//gyro reset
    private DigitalInput faceUp;//rotation lock 0 degrees
    private DigitalInput faceRight;//rotation lock 90 degrees
    private DigitalInput faceLeft;//rotation lock 270 degrees
    private DigitalInput faceDown;//rotation lock 180 degrees
    private DigitalInput driverStart; // Auto rotate to reef
    private DigitalInput operatorLeftBumper; // Select left branch auto align
    private DigitalInput operatorRightBumper; // Select right branch auto align
    private DigitalInput operatorFaceLeft;
    private DigitalInput operatorStart;
    private DigitalInput operatorSelect;
    private WsJoystickAxis operatorRightTrigger;
    private WsJoystickAxis operatorLeftTrigger;
    private WsJoystickButton operatorX;

    private double gyroReading; // Reading from gyro CW degrees

    private double horizontalPower;
    private double verticalPower;

    public boolean autoUsePID = true;

    private swerveCTRE swerve = new swerveCTRE(DriveConstants.swerveCTREconsts, 
        DriveConstants.frontLeft, DriveConstants.frontRight, DriveConstants.backLeft, DriveConstants.backRight);

    private SwerveSignal swerveSignal = new SwerveSignal();

    private WsSwerveHelper swerveHelper = new WsSwerveHelper();
    StructArrayPublisher<SwerveModuleState> moduleStatePublisher = NetworkTableInstance.getDefault().getStructArrayTopic("ModuleStates", SwerveModuleState.struct).publish();
    public ChassisSpeeds speeds;


    private WsPose pose;

    private Translation2d coralPoint;
    private Pose2d targetPose;
    StructPublisher<Pose2d> targetPosePublisher = NetworkTableInstance.getDefault().getStructTopic("targetPose", Pose2d.struct).publish();


    public enum DriveType {TELEOP, AUTO, CORALINTAKE};
    private DriveType driveState;

    @Override
    public void inputUpdate(Input source) {
        
        
        // If none of those conditions are met, return to Teleop mode
        //} else {
            driveState = DriveType.TELEOP;
        //}

        if (driveState == DriveType.AUTO) driveState = DriveType.TELEOP;

        //get x and y speeds
        horizontalPower = swerveHelper.scaleDeadband(leftStickHorizontal.getValue(), DriveConstants.DEADBAND);
        verticalPower = swerveHelper.scaleDeadband(leftStickVertical.getValue(), DriveConstants.DEADBAND);
        
        
        //reset gyro
        if (source == select && select.getValue()) {
            swerve.resetRotation(new Rotation2d());
            swerveSignal.setRotLocked(0);
        }

        // Cardinal directions
        if (source == faceUp && faceUp.getValue()){
            if (faceLeft.getValue()) swerveSignal.setRotLocked(60);
            else if (faceRight.getValue()) swerveSignal.setRotLocked(300);
            else  swerveSignal.setRotLocked(0);
        }
        if (source == faceLeft && faceLeft.getValue()){
            if (faceUp.getValue()) swerveSignal.setRotLocked(60);
            else if (faceDown.getValue()) swerveSignal.setRotLocked(120);
            else swerveSignal.setRotLocked(90);
        }
        if (source == faceDown && faceDown.getValue()){
            if (faceLeft.getValue()) swerveSignal.setRotLocked(120);
            else if (faceRight.getValue()) swerveSignal.setRotLocked(240);
            else swerveSignal.setRotLocked(180);
        }
        if (source == faceRight && faceRight.getValue()){
            if (faceUp.getValue()) swerveSignal.setRotLocked(300);
            else if (faceDown.getValue()) swerveSignal.setRotLocked(240);
            else swerveSignal.setRotLocked(270);
        }

        //get rotational joystick
        double rotSpeed = rightStickHorizontal.getValue()*Math.abs(rightStickHorizontal.getValue());
        rotSpeed = swerveHelper.scaleDeadband(rotSpeed, DriveConstants.DEADBAND);
        //if the rotational joystick is being used, the robot should not be auto tracking heading
        if (rotSpeed != 0) {
            swerveSignal.setFreeRotation(rotSpeed);
        }
    }
 
    @Override
    public void init() {
        initInputs();
        resetState();
        swerve.resetRotation(new Rotation2d());
    }

    public void initSubsystems() {
        pose = (WsPose) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_POSE);
    }

    public void initInputs() {
        leftStickHorizontal = (AnalogInput) WsInputs.DRIVER_LEFT_JOYSTICK_HORIZONTAL.get();
        leftStickHorizontal.addInputListener(this);
        leftStickVertical = (AnalogInput) WsInputs.DRIVER_LEFT_JOYSTICK_VERTICAL.get();
        leftStickVertical.addInputListener(this);
        rightStickHorizontal = (AnalogInput) WsInputs.DRIVER_RIGHT_JOYSTICK_HORIZONTAL.get();
        rightStickHorizontal.addInputListener(this);
        rightTrigger = (AnalogInput) WsInputs.DRIVER_RIGHT_TRIGGER.get();
        rightTrigger.addInputListener(this);
        leftTrigger = (AnalogInput) WsInputs.DRIVER_LEFT_TRIGGER.get();
        leftTrigger.addInputListener(this);
        rightBumper = (DigitalInput) WsInputs.DRIVER_RIGHT_SHOULDER.get();
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        select = (DigitalInput) WsInputs.DRIVER_SELECT.get();
        select.addInputListener(this);
        faceUp = (DigitalInput) WsInputs.DRIVER_FACE_UP.get();
        faceUp.addInputListener(this);
        faceLeft = (DigitalInput) WsInputs.DRIVER_FACE_LEFT.get();
        faceLeft.addInputListener(this);
        faceRight = (DigitalInput) WsInputs.DRIVER_FACE_RIGHT.get();
        faceRight.addInputListener(this);
        faceDown = (DigitalInput) WsInputs.DRIVER_FACE_DOWN.get();
        faceDown.addInputListener(this);
        driverStart = (DigitalInput) WsInputs.DRIVER_START.get();
        driverStart.addInputListener(this);
        operatorLeftBumper = (DigitalInput) WsInputs.OPERATOR_LEFT_SHOULDER.get();
        operatorLeftBumper.addInputListener(this);
        operatorRightBumper = (DigitalInput) WsInputs.OPERATOR_RIGHT_SHOULDER.get();
        operatorRightBumper.addInputListener(this);
        operatorFaceLeft = (DigitalInput) WsInputs.OPERATOR_FACE_LEFT.get();
        operatorFaceLeft.addInputListener(this);
        operatorSelect = (DigitalInput) WsInputs.OPERATOR_SELECT.get();
        operatorSelect.addInputListener(this);
        operatorStart = (DigitalInput) WsInputs.OPERATOR_START.get();
        operatorStart.addInputListener(this);
        operatorLeftTrigger = (WsJoystickAxis) WsInputs.OPERATOR_LEFT_TRIGGER.get();
        operatorLeftTrigger.addInputListener(this);
        operatorRightTrigger = (WsJoystickAxis) WsInputs.OPERATOR_RIGHT_TRIGGER.get();
        operatorRightTrigger.addInputListener(this);
        operatorX = (WsJoystickButton) WsInputs.OPERATOR_FACE_LEFT.get();
        operatorX.addInputListener(this);
    }

    
    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        gyroReading = Math.toDegrees(swerve.getRotation3d().getAngle());
        Logger.processInputs("Swerve", this);
        pose.addOdometryObservation(swerve.getState().Pose);
        // Reset coral point
        if (driveState != DriveType.CORALINTAKE) {
            coralPoint = null;
            if (driveState != DriveType.AUTO) {
                pose.setPipelineObject(false);
            } else pose.setPipelineObject(true);
        }

        if (driveState == DriveType.TELEOP) {
            this.swerveSignal.setTranslation(horizontalPower, verticalPower);

        // If we want to use object detection pipeline to align to coral
        // Aligns heading to face coral and then p-loop to intake it
        // Keeps driving to last seen point but doesn't turn if it can't see a coral
        } else if (driveState == DriveType.CORALINTAKE) {
            pose.setPipelineObject(true);
            // Update point
            if (pose.getCoralPose().isPresent()) {
                Translation2d newCoralPoint = pose.getCoralPose().get();
                // Avoid updating to a new coral after intaking
                // Only update coralpoint if newCoralPoint is less than .5 meters further away from our robot
                if (coralPoint == null) {
                    coralPoint = newCoralPoint;
                } else if (pose.estimatedPose.getTranslation().getDistance(newCoralPoint) - pose.estimatedPose.getTranslation().getDistance(coralPoint) < 0.5) {
                    coralPoint = newCoralPoint;
                }
            }
            // if (coralPoint != null && !coralPath.hasCoral()) {
            //     // Account for intake position so when our robot is at intakeAdjustedPoint the ground intake is centered on the coral
            //     Translation2d intakeAdjustedPoint = new  Pose2d(coralPoint, odoAngle()).plus(VisionConsts.intakeOffset.inverse()).getTranslation();
            //     targetPosePublisher.set(new Pose2d(intakeAdjustedPoint, new Rotation2d()));
            //     swerveSignal.setDriveToPoint(new Pose2d(intakeAdjustedPoint, Rotation2d.fromDegrees(pose.turnToTarget(intakeAdjustedPoint))));
            // } else {
                this.swerveSignal.setTranslation(horizontalPower, verticalPower);
            // }
            
        } else if (driveState == DriveType.AUTO) {
            swerveSignal.setDriveToPoint(targetPose);
        }
            
        swerve.setControl(swerveSignal.drive());
        SmartDashboard.putNumber("X Power", horizontalPower);
        SmartDashboard.putNumber("Y Power", verticalPower);
        SmartDashboard.putNumber("# Robot X", pose.estimatedPose.getX());
        SmartDashboard.putNumber("# Robot Y", pose.estimatedPose.getY());
        SmartDashboard.putNumber("Gyro Reading", getGyroAngle());
        SmartDashboard.putNumber("rotSpeed", swerveSignal.getRotation());
        SmartDashboard.putString("Drive mode", driveState.toString());
        SmartDashboard.putString("Drive Control", swerveSignal.currentState());
        SmartDashboard.putNumber("Rotation target", swerveSignal.getRotTarget());
        SmartDashboard.putNumber("Yaw", swerve.getPigeon2().getYaw().getValueAsDouble());
        SmartDashboard.putNumber("Roll", swerve.getPigeon2().getRoll().getValueAsDouble());
        SmartDashboard.putNumber("Pitch", swerve.getPigeon2().getPitch().getValueAsDouble());
        SmartDashboard.putNumber("@ mega2 gyro", getMegaTag2Yaw());
        SmartDashboard.putNumber("@ speed", speedMagnitude());
        if (targetPose != null){
            targetPosePublisher.set(targetPose);
        }
        moduleStatePublisher.set(moduleStates());
    }
    
    @Override
    public void resetState() {
        gyroReading = 0;
        horizontalPower = 0;
        verticalPower = 0;
        swerveSignal.setFreeRotation(0);
        setToTeleop();
    }

    @Override
    public String getName() {
        return "Swerve Drive";
    }

    /** sets the drive to teleop/cross, and sets drive motors to coast */
    public void setToTeleop() {
        driveState = DriveType.TELEOP;
        swerveSignal.setFreeRotation(0);
        horizontalPower = 0;
        verticalPower = 0;
    }

    /**sets the drive to autonomous */
    public void setToAuto() {
        driveState = DriveType.AUTO;
    }

    // Sets autonomous values from the motion profile in Driver Station relative 
    public void setAutoValues(double xVelocity, double yVelocity, double xAccel, double yAccel, Pose2d target) {
        SmartDashboard.putNumber("Path Velocity", Math.sqrt(Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2)));
        // accel of 0 because currently not using acceleration for power since
        horizontalPower = swerveHelper.getAutoPower(xVelocity, xAccel);
        verticalPower = swerveHelper.getAutoPower(yVelocity, yAccel);
        targetPose = target;
    }

    // Sets autonomous values when driving to a pose and not using a motion profile
    public void setAutoValues(Pose2d target) {
        setAutoValues(0, 0, 0, 0, target);
    }

    public void usePID(boolean use) {
        autoUsePID = use;
    }

    /** sets the autonomous heading controller to a new target */
    @Deprecated
    public void setAutoHeading(double headingTarget) {
        swerveSignal.setRotLocked(headingTarget);
    }

    /**
     * Resets the gyro, and sets it the input number of degrees
     * Used for starting the match at a non-0 angle
     * @param degrees the current value the gyro should read
     */
    public void setGyro(double degrees) {
        resetState();
        setToAuto();

        // Make degrees clockwise
        swerve.resetRotation(new Rotation2d(Math.toRadians(degrees)));
    }

    public double getGyroAngle() {
        return gyroReading;
    }  

    /**
     * Doesn't need to use gyroReading since it isn't used as an input
     * @return Returns the field relative CCW gyro angle for use with Limelight MegaTag2
     */
    public double getMegaTag2Yaw(){
        return (Math.toDegrees(swerve.getRotation3d().getAngle()) + (Core.isBlue() ? 0 : 180));
    }
    /** 
     * @return Returns alliance relative CCW Rotation2d gyro angle for use with always alliance relative pose
     */
    public Rotation2d odoAngle(){
        return Rotation2d.fromDegrees(gyroReading);
    }
    public double speedMagnitude(){
        return Math.sqrt(Math.pow(speeds().vxMetersPerSecond, 2) + Math.pow(speeds().vyMetersPerSecond, 2));
    }

    private ChassisSpeeds speeds() {
        return swerve.getState().Speeds;
    }

    public SwerveModuleState[] moduleStates() {
        return new SwerveModuleState[]{swerve.getModule(0).getCurrentState(), swerve.getModule(1).getCurrentState(), 
            swerve.getModule(2).getCurrentState(), swerve.getModule(3).getCurrentState()};
    }

    private boolean frontCloser(double targetAngle) {
        return (WsSwerveHelper.angleDist(targetAngle, getGyroAngle()) < 90);
    }

    // SUBSYSTEM ACCESS METHODS

    public void setDriveState(DriveType state) {
        driveState = state;
    }

    public boolean isAtPosition() {
        // If in coral intake mode, then compare the position + intake offset to the coralPoint
        // if (driveState == DriveType.CORALINTAKE) {

        //     // Return false if not seeing a coral yet
        //     return coralPoint != null ? pose.estimatedPose.plus(VisionConsts.intakeOffset).getTranslation().getDistance(coralPoint) < DriveConstants.POSITION_TOLERANCE : false;
        // } else {
            return isAtPosition(DriveConstants.POSITION_TOLERANCE);
        // }
    }
    public boolean isAtPosition(double tolerance) {
        return pose.estimatedPose.getTranslation().getDistance(targetPose.getTranslation()) < tolerance && WsSwerveHelper.angleDist(pose.estimatedPose.getRotation().getDegrees(), targetPose.getRotation().getDegrees()) < 2.5;
    }
    public double distanceToTarget(){
        return pose.estimatedPose.getTranslation().getDistance(targetPose.getTranslation());
    }


    @Override
    public void toLog(LogTable table) {
        table.put("gyroReading", gyroReading);
    }

    @Override
    public void fromLog(LogTable table) {
        gyroReading = table.get("gyroReading", gyroReading);
    }
}
