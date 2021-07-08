package org.wildstang.sample.robot;

/**
 * CAN Constants are stored here.
 * We primarily use CAN to communicate with Talon motor controllers.
 * These constants must correlate with the IDs set in Phoenix Tuner.
 * Official documentation can be found here:
 * https://phoenix-documentation.readthedocs.io/en/latest/ch08_BringUpCAN.html
 */
public final class CANConstants {

    public static final int[] DRIVE_MOTOR_CONTROLLERS       = {1,2};
    public static final int   OUTTAKE_MOTOR_CONTROLLER      = 3;
    public static final int   HIGHFUEL_MOTOR_CONTROLLER     = 4;
    public static final int   INTAKE_MOTOR_CONTROLLER       = 5;
    
}