package org.wildstang.template.robot;

/**
 * CAN Constants are stored here.
 * We primarily use CAN to communicate with SparkMax motor controllers.
 * These constants must correlate with the IDs set in REV Hadware Client.
 * Official documentation can be found here:
 * https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces#can-interface
 * https://phoenix-documentation.readthedocs.io/en/latest/ch08_BringUpCAN.html
 */
public final class CANConstants {

    // Gyro and CAN sensor constants
    public static final int GYRO = 31;

    // Swerve constants
    public static final int DRIVE1 = 11;
    public static final int ANGLE1 = 12;
    public static final int DRIVE2 = 13;
    public static final int ANGLE2 = 14;
    public static final int DRIVE3 = 15;
    public static final int ANGLE3 = 16;
    public static final int DRIVE4 = 17;
    public static final int ANGLE4 = 18;
    
}