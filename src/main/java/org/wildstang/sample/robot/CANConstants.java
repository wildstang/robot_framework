package org.wildstang.sample.robot;

import java.util.Map;

/**
 * CAN Constants are stored here.
 * We primarily use CAN to communicate with Talon motor controllers.
 * These constants must correlate with the IDs set in Phoenix Tuner.
 * Official documentation can be found here:
 * https://phoenix-documentation.readthedocs.io/en/latest/ch08_BringUpCAN.html
 */
public final class CANConstants {

    // Replace these examples.
    // While not independently dangerous if implemented these could have unintended effects.
    //public static final int[] EXAMPLE_PAIRED_CONTROLLERS    = {1,2};
    //public static final int   EXAMPLE_MOTOR_CONTROLLER      = 3;

    //Gyro and CAN sensor values
    public static final int GYRO = 31;

    //swerve constants
    public static final int DRIVE1 = 11; // FL
    public static final int ANGLE1 = 12;
    public static final int DRIVE2 = 13; // FR
    public static final int ANGLE2 = 14;
    public static final int DRIVE3 = 15; // BL
    public static final int ANGLE3 = 16;
    public static final int DRIVE4 = 17; // BR
    public static final int ANGLE4 = 18;

    public static final int FL_ENCODER = 41;
    public static final int FR_ENCODER = 42;
    public static final int BL_ENCODER = 43;
    public static final int BR_ENCODER = 44;

    // only needed for REV logging
    public static final Map<Integer, String> aliasMap = Map.ofEntries(
        Map.entry(DRIVE1, "DRIVE1"),
        Map.entry(ANGLE1, "ANGLE1"),
        Map.entry(DRIVE2, "DRIVE2"),
        Map.entry(ANGLE2, "ANGLE2"),
        Map.entry(DRIVE3, "DRIVE3"),
        Map.entry(ANGLE3, "ANGLE3"),
        Map.entry(DRIVE4, "DRIVE4"),
        Map.entry(ANGLE4, "ANGLE4")
    );
}