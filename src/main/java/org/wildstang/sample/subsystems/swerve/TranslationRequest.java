package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.swerve.SwerveDrivetrain.SwerveControlParameters;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveModule.SteerRequestType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;

public class TranslationRequest implements SwerveRequest{

    private final FieldCentricFacingAngle request = new FieldCentricFacingAngle()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
        .withSteerRequestType(SteerRequestType.MotionMagicExpo);
    public Pose2d targetPose = new Pose2d();

    @Override
    public StatusCode apply(SwerveControlParameters arg0, SwerveModule<?, ?, ?>... arg1) {

        return request
            .withVelocityX(DriveConstants.ALIGN_P * (targetPose.getX() - arg0.currentPose.getX()))
            .withVelocityY(DriveConstants.ALIGN_P * (targetPose.getY() - arg0.currentPose.getY()))
            .withTargetDirection(new Rotation2d(0))
            .apply(arg0, arg1);
    }
    public void setTarget(Pose2d target){
        this.targetPose = target;
    }
    
}
