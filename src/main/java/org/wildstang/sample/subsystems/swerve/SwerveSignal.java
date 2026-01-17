package org.wildstang.sample.subsystems.swerve;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModule.SteerRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.utility.PhoenixPIDController;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;

public class SwerveSignal {
    private double verticalSpeed = 0;
    private double horizontalSpeed = 0;
    private double rotationSpeed = 0;
    private double rotationTarget = 0;
    private Pose2d drivingPose = new Pose2d();
    public enum controlState {MANUAL, ROTLOCKED, DRIVETOPOINT, X_DRIVETOPOINT, Y_DRIVETOPOINT}
    private controlState state = controlState.MANUAL;
    private SwerveRequest.FieldCentric driveCommand = new SwerveRequest.FieldCentric()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.MotionMagicExpo);
    private SwerveRequest.FieldCentricFacingAngle driveLockedCommand = new SwerveRequest.FieldCentricFacingAngle()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.MotionMagicExpo);
    private TranslationRequest translationRequest = new TranslationRequest();
    private XTranslationRequest xTranslationRequest = new XTranslationRequest();
    private YTranslationRequest yTranslationRequest = new YTranslationRequest();    

    public SwerveSignal(){
        this(0,0,0);
    }
    public SwerveRequest drive(){
        if (state == controlState.ROTLOCKED){
            return driveLockedCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withTargetDirection(new Rotation2d(Math.toRadians(getRotTarget())));
            //.withTargetRateFeedforward(double) to potentially include rotation rate
        } else if (state == controlState.DRIVETOPOINT){
            translationRequest.setTarget(getDriveToPoint());
            return translationRequest;
        } else if (state == controlState.X_DRIVETOPOINT){
            xTranslationRequest.setTarget(getDriveToPoint(), getHorizontal());
            return xTranslationRequest;
        } else if (state == controlState.Y_DRIVETOPOINT){
            yTranslationRequest.setTarget(getDriveToPoint(), getVertical());
            return yTranslationRequest;
        } else {//} else if (state == controlState.MANUAL){
            return driveCommand.withVelocityX(getVertical() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withVelocityY(getHorizontal() * DriveConstants.maxSpeed.in(MetersPerSecond))
                .withRotationalRate(getRotation() * RotationsPerSecond.of(0.75).in(RadiansPerSecond));
        }
    }
    public SwerveSignal(double vert, double hori, double rot){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        this.rotationSpeed = rot;
        driveLockedCommand.HeadingController = new PhoenixPIDController(DriveConstants.heading_P, 0.0, DriveConstants.heading_D);
        driveLockedCommand.HeadingController.enableContinuousInput(-Math.PI, Math.PI);
    }
    public void setTranslation(double vert, double hori){
        this.verticalSpeed = vert;
        this.horizontalSpeed = hori;
        if (state != controlState.ROTLOCKED) state = controlState.MANUAL;
    }
    public void setFreeRotation(double rotation){
        state = controlState.MANUAL;
        rotationSpeed = rotation;
    }
    public void setRotLocked(double rotation){
        state = controlState.ROTLOCKED;
        rotationTarget = rotation;
    }
    public void setDriveToPoint(Pose2d newTarget){
        drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.DRIVETOPOINT;
    }
    public void setXDriveToPoint(double hori, Pose2d newTarget){
        this.horizontalSpeed = hori;
        this.drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.X_DRIVETOPOINT;
    }
    public void setYDriveToPoint(double vert, Pose2d newTarget){
        this.verticalSpeed = vert;
        this.drivingPose = newTarget;
        rotationTarget = newTarget.getRotation().getDegrees();
        state = controlState.Y_DRIVETOPOINT;
    }
    public double getVertical(){ 
        return verticalSpeed;
    }
    public double getHorizontal(){ 
        return horizontalSpeed;
    }
    public double getRotation(){
        return rotationSpeed;
    }
    public double getRotTarget(){
        return rotationTarget;
    }
    public Pose2d getDriveToPoint(){
        return drivingPose;
    }
    public String currentState(){
        return state.toString();
    }
    public boolean isRotLocked(){
        return state == controlState.ROTLOCKED;
    }
    public boolean isManual(){
        return state == controlState.MANUAL;
    }
    public boolean isTranslate(){
        return state == controlState.DRIVETOPOINT;
    }
    public boolean isXTranslate(){
        return state == controlState.X_DRIVETOPOINT;
    }
    public boolean isYTranslate(){
        return state == controlState.Y_DRIVETOPOINT;
    }
}
