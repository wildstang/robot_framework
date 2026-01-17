package org.wildstang.sample.subsystems.swerve;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;

public class swerveCTRE extends SwerveDrivetrain<TalonFX, TalonFX, CANcoder> {
    public swerveCTRE(SwerveDrivetrainConstants consts, SwerveModuleConstants<?, ?, ?>... modules){
        super(TalonFX::new, TalonFX::new,  CANcoder::new, consts, modules);
    }
}