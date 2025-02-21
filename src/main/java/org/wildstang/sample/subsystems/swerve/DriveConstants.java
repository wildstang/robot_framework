package org.wildstang.sample.subsystems.swerve;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

public final class DriveConstants {
    public static final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(new Translation2d(0.2794, 0.33), new Translation2d(0.2794, -0.33),
            new Translation2d(-0.2794, 0.33), new Translation2d(-0.2794, -0.33));
    /**P Loop for Translating to a point */
    public static final double TRANSLATION_P = 0.0;
    /** robot length from swerve pod to swerve pod, in inches */
    public static final double ROBOT_LENGTH = 11.5;
    /** robot width from swerve pod to swerve pod, in inches */
    public static final double ROBOT_WIDTH = 13.08;
    /**speed with which the robot rotates relative to drive speed */
    public static final double ROTATION_SPEED = 0.75;
    /**speed with which the rotation PID is controlled by */
    public static final double PID_ROTATION = 1.5;
    /**drive motor gear ratio */
    public static final double DRIVE_RATIO = 4.0;
    /**angle motor gear ratio */
    //public static final double ANGLE_RATIO = 12.8;
    /**diameter of drive wheel, in inches */
    public static final double WHEEL_DIAMETER = 2.73;
    /**offset of module 1, the front left module, in degrees */
    public static final double FRONT_LEFT_OFFSET = -90;
    /**offset of module 2, the front right module, in degrees */
    public static final double FRONT_RIGHT_OFFSET = 0;
    /**offset of module 3, the rear left module, in degrees */
    public static final double REAR_LEFT_OFFSET = 180;
    /**offset of module 4, the rear right module, in degrees */
    public static final double REAR_RIGHT_OFFSET = 90;
    /**deadband of the controller's joysticks */
    public static final double DEADBAND = 0.05;
    /**factor of thrust for the drive trigger */
    public static final double DRIVE_THRUST = 0.4;
    /**end game drive brake */
    public static final double DRIVE_BRAKE = 0.4;
    /**slew rate limiter rates of limit for the drive
    *  value is max change per second
    *  i.e. 2.0 means it can go from 0 to 1.0 in 0.5 seconds
    */
    public static final double SLEW_RATE_LIMIT = 3.0;
    /**second order correction for rotation plus driving */
    public static final double ROT_CORRECTION_FACTOR = -0.5;
    /**encoder ticks per revolution, 1.0 for neos */
    public static final double TICKS_PER_REV = 1.0;
    /**PID values for drive P */
    public static final double DRIVE_P = 0;//-0.02;//0.02
    /**PID values for drive I */
    public static final double DRIVE_I = 0.01;
    /**PID values for drive D */
    public static final double DRIVE_D = 0.1;
    /**PID values for driveF coefficient of momentum */
    public static final double DRIVE_F_V = 0.00375 * 157.5/144.5*157.5/142;//free speed of 22.2 ft/s becomes 266.4 in/s
    /**PID values for drive F coefficient of kinetic friction */
    public static final double DRIVE_F_K = 0.016;
    /**PID values for drive F coefficient of inertia */
    public static final double DRIVE_F_I = 0.000;//
    /**PID values for angle P */
    public static final double ANGLE_P = 0.01;
    /**PID values for angle I */
    public static final double ANGLE_I = 0.0;
    /**PID values for angle D */
    public static final double ANGLE_D = 0.0;
    /**Drive motor current limit */
    public static final int DRIVE_CURRENT_LIMIT = 60;
    /**Angle motor current limit */
    public static final int ANGLE_CURRENT_LIMIT = 10;
    /**Swerve Module Names */
    public static final String[] POD_NAMES = new String[]{"FL", "FR", "BL", "BR"};

}
