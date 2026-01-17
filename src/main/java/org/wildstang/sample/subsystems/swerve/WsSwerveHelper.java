package org.wildstang.sample.subsystems.swerve;

public class WsSwerveHelper {

    // Distance between two angles in degrees between
    public static double angleDist(double angle1, double angle2) {
        double distance = Math.abs((angle1 % 360) - (angle2 % 360));

        // If that distance gives us the long way
        if (distance > 180) {
            return 360 - distance;
        } 
        return distance;
    }

    /*
     * Determines if the limelight should take over for object detection in auto
     * If the robot is moving too slowly to pick up a detected gamepiece, gives power
     * to move enough to pick up the gamepiece
     * @param ty ty value of limelight in object detection mode
     * @param xPower X value of the normal auto path
     * @param yPower Y value of the normal auto path
     * @return the new power value to use if the robot is moving too slowly, otherwise 0 if the robot is fine
     */
    public double adjustObjectAuto( double ty, double xPower, double yPower){
        double yObject = ty*0.03;
        if (Math.hypot(xPower, yPower) > yObject) return yObject;
        else return 0;
    }

    /**determines the translational magnitude of the robot in autonomous
     * 
     * @param pathVel path data for velocity of the robot (m/s)
     * @param pathAccel path data for acceleration of the robot (m/s^2)
     * @return double for magnitude of translational vector
     */
    public double getAutoPower(double pathVel, double pathAccel) {
        if (pathVel == 0) return 0;
        // I don't know why this was negative
        return (pathVel * DriveConstants.DRIVE_F_V + DriveConstants.DRIVE_F_K + pathAccel * DriveConstants.DRIVE_F_I);
    }

    /**returns magnitude of vector components */
    public double getMagnitude(double x, double y) {
        return Math.hypot(x, y);
    }

    /**x,y inputs are cartesian, angle values are in bearing, returns 0 - 360 */
    public double getDirection(double x, double y) {
        double measurement = Math.toDegrees(Math.atan2(x,y));//returns angle in bearing form
        if (measurement < 0) {
            measurement = 360 + measurement;
        }
        else if (measurement >= 360) {
            measurement = measurement - 360;
        }
        return measurement;
    }
    
    public double scaleDeadband(double input, double deadband){
        if (Math.abs(input) < Math.abs(deadband)) return 0.0;
        return Math.signum(input)*((Math.abs(input) - deadband) / (1.0 - deadband));
    }
    
}
