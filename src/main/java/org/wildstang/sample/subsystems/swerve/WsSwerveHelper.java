package org.wildstang.sample.subsystems.swerve;

public class WsSwerveHelper {

    private double magnitude;
    private double direction;
    private SwerveSignal swerveSignal;
    private double rotMag;
    private double baseV;
    private double[] xCoords = new double[]{0.0, 0.0, 0.0, 0.0};
    private double[] yCoords = new double[]{0.0, 0.0, 0.0, 0.0};
    private double rotDelta;
    private double rotPID;

    /** sets the robot in the immobile "cross" defensive position
     * 
     * @return driveSignal for cross, with 0 magnitude and crossed directions
     */
    public SwerveSignal setCross() {
        return new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{135.0, 45.0, 45.0, 135.0});
    }

    /** sets the robot in the mobile defensive "crab" mode, where all modules are aligned
     * @param i_tx the translation joystick x value
     * @param i_ty the translation joystick y value
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return driveSignal for crab, where all modules are the same direction/power
     */
    public SwerveSignal setCrab(double i_tx, double i_ty, double i_gyro) {
        magnitude = getMagnitude(i_tx, i_ty);
        direction = getDirection(i_tx, i_ty);
        swerveSignal = new SwerveSignal(new double[]{magnitude, magnitude, magnitude, magnitude}, new double[]{direction, direction, direction, direction});
        swerveSignal.normalize();
        return swerveSignal;
    }

    /**sets the robot to drive normally as a swerve
     * 
     * @param i_tx the translation joystick x value
     * @param i_ty the translation joystick y value
     * @param i_rot the rotation joystick value
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return driveSignal for normal driving, normalized
     */
    public SwerveSignal setDrive(double i_tx, double i_ty, double i_rot, double i_gyro) {
        //magnitude of rotation vector
        rotMag = i_rot * DriveConstants.ROTATION_SPEED;
        //angle of front left rotation vector
        baseV = Math.atan(DriveConstants.ROBOT_LENGTH / DriveConstants.ROBOT_WIDTH);

        //find the translational vectors rotated to account for the gyro
        double xTrans = i_tx * Math.cos(Math.toRadians(i_gyro)) - i_ty * Math.sin(Math.toRadians(i_gyro));
        double yTrans = i_tx * Math.sin(Math.toRadians(i_gyro)) + i_ty * Math.cos(Math.toRadians(i_gyro));

        //account for slight second order skew due to rotation and translation at the same time
        xTrans += Math.cos(Math.atan2(xTrans,yTrans)) * i_rot * DriveConstants.ROT_CORRECTION_FACTOR * Math.hypot(i_tx, i_ty);
        yTrans += -Math.sin(Math.atan2(xTrans,yTrans)) * i_rot * DriveConstants.ROT_CORRECTION_FACTOR * Math.hypot(i_tx, i_ty);

        //cartesian vector addition of translation and rotation vectors
        //note rotation vector angle advances in the cos -> sin -> -cos -> -sin fashion
        xCoords = new double[]{xTrans + rotMag * Math.cos(baseV), xTrans + rotMag*Math.sin(baseV), xTrans - rotMag * Math.sin(baseV), xTrans - rotMag*Math.cos(baseV)}; 
        yCoords = new double[]{yTrans + rotMag * Math.sin(baseV), yTrans - rotMag*Math.cos(baseV), yTrans + rotMag * Math.cos(baseV), yTrans - rotMag*Math.sin(baseV)};

        //create drivesignal, with magnitudes and directions of x and y
        swerveSignal = new SwerveSignal(new double[]{getMagnitude(xCoords[0], yCoords[0]), getMagnitude(xCoords[1], yCoords[1]), getMagnitude(xCoords[2], yCoords[2]), getMagnitude(xCoords[3], yCoords[3])}, 
            new double[]{getDirection(xCoords[0], yCoords[0]), getDirection(xCoords[1], yCoords[1]), getDirection(xCoords[2], yCoords[2]), getDirection(xCoords[3], yCoords[3])});
        swerveSignal.normalize();
        if (swerveSignal.isNotZeroed()) {
            return swerveSignal;
        }
        else {
            return this.setDrive(i_tx, i_ty, i_rot * 0.8, i_gyro);
        }
    }

    /**sets the robot to move in autonomous
     * 
     * @param i_power magnitude of translational vector, in signal [0,1]
     * @param i_heading direction of translational vector, in field centric bearing degrees
     * @param i_rot the rotational joystick value, created by the heading controller
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return SwerveSignal that is the command for the robot to move
     */
    public SwerveSignal setAuto(double i_X, double i_Y, double i_rot, double i_gyro, double xOffset, double yOffset) {
        //return setDrive(i_power * -Math.sin(Math.toRadians(i_heading))+ xOffset*.5, i_power * -Math.cos(Math.toRadians(i_heading))+ yOffset*.5, i_rot, i_gyro); //TODO: change xxoffset multiplier
        return setDrive(i_X - xOffset*Math.cos(Math.toRadians(i_gyro)) - yOffset*Math.sin(Math.toRadians(i_gyro)), 
            i_Y + xOffset*Math.sin(Math.toRadians(i_gyro)) - yOffset*Math.cos(Math.toRadians(i_gyro)), i_rot, i_gyro); 
    }

    /*
     * Sets the robot to automatially drive at a detected game piece
     * Adjusts driver's joysticks to point at gamepiece, and rotates the robot to face the gamepiece
     * 
     * @param xPower X value of joystick
     * @param yPower Y value of joystick
     * @param tx limelight camera's tx value looking at gamepiece 
     * @return the swerveSignal to control the swerveDrive
     */
    public SwerveSignal setObject(double xPower, double yPower, double tx){
        return setDrive(0, -Math.hypot(yPower, xPower) , getRotControl(tx, 0.0), 360.0-1.0*tx);
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

    /**automatically creates a rotational joystick value to rotate the robot towards a specific target
     * 
     * @param i_target target direction for the robot to face, field centric, bearing degrees
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return double that indicates what the rotational joystick value should be
     */
    public double getRotControl(double i_target, double i_gyro) {
        rotDelta = i_target - i_gyro;
        if (rotDelta > 180) {
            rotPID = (rotDelta - 360) / 180;
        }
        else if (Math.abs(rotDelta) <= 180.0) {
            rotPID = rotDelta / 180.0;
        }
        else {
            rotPID = (rotDelta + 360) / 180;
        } 
        return Math.signum(rotPID) * Math.min(Math.abs(rotPID*DriveConstants.PID_ROTATION), 1.0/DriveConstants.ROTATION_SPEED);
    }
    /*
     * getRotControl, but capped to -0.2 to 0.2
     * 
     * @param i_target target direction for the robot to face, field centric, bearing degrees
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return double that indicates what the rotational joystick value should be
     */
    public double getAutoRotation(double i_target, double i_gyro){
        return Math.max(-0.2, Math.min(0.2, getRotControl(i_target, i_gyro)));
    }

    /**determines the translational magnitude of the robot in autonomous
     * 
     * @param pathVel path data for velocity of the robot, inches
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
