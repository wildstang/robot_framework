package org.wildstang.sample.subsystems.swerve;

public class WSSwerveHelper {

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
    public SwerveSignal setCross(){
        return new SwerveSignal(new double[]{0.0, 0.0, 0.0, 0.0}, new double[]{135.0, 45.0, 45.0, 135.0});
    }

    /** sets the robot in the mobile defensive "crab" mode, where all modules are aligned
     * @param i_tx the translation joystick x value
     * @param i_ty the translation joystick y value
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return driveSignal for crab, where all modules are the same direction/power
     */
    public SwerveSignal setCrab(double i_tx, double i_ty, double i_gyro){
        magnitude = getMagnitude(i_tx, i_ty);
        direction = getDirection(i_tx, i_ty, i_gyro);
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
    public SwerveSignal setDrive(double i_tx, double i_ty, double i_rot, double i_gyro){
        //magnitude of rotation vector
        rotMag = i_rot * DriveConstants.ROTATION_SPEED;
        //angle of front left rotation vector
        baseV = Math.atan(DriveConstants.ROBOT_LENGTH / DriveConstants.ROBOT_WIDTH);

        //cartesian vector addition of translation and rotation vectors
        //note rotation vector angle advances in the cos -> sin -> -cos -> -sin fashion
        xCoords = new double[]{i_tx + rotMag*Math.cos(baseV), i_tx + rotMag*Math.sin(baseV), i_tx - rotMag*Math.cos(baseV), i_tx - rotMag*Math.sin(baseV)}; 
        yCoords = new double[]{i_ty + rotMag*Math.sin(baseV), i_ty - rotMag*Math.cos(baseV), i_ty - rotMag*Math.sin(baseV), i_ty + rotMag*Math.cos(baseV)};

        //create drivesignal, with magnitudes and directions of x and y
        swerveSignal = new SwerveSignal(new double[]{getMagnitude(xCoords[0], yCoords[0]), getMagnitude(xCoords[1], yCoords[1]), getMagnitude(xCoords[2], yCoords[2]), getMagnitude(xCoords[3], yCoords[3])}, 
            new double[]{getDirection(xCoords[0], yCoords[0], i_gyro), getDirection(xCoords[1], yCoords[1], i_gyro), getDirection(xCoords[2], yCoords[2], i_gyro), getDirection(xCoords[3], yCoords[3], i_gyro)});
        swerveSignal.normalize();
        return swerveSignal;
    }

    /**sets the robot to move in autonomous
     * 
     * @param i_power magnitude of translational vector, in signal [0,1]
     * @param i_heading direction of translational vector, in field centric bearing degrees
     * @param i_rot the rotational joystick value, created by the heading controller
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return SwerveSignal that is the command for the robot to move
     */
    public SwerveSignal setAuto(double i_power, double i_heading, double i_rot, double i_gyro){
        return setDrive(i_power * Math.cos(Math.toRadians(i_heading)), i_power * Math.sin(Math.toRadians(i_heading)), i_rot, i_gyro);
    }

    /**automatically creates a rotational joystick value to rotate the robot towards a specific target
     * 
     * @param i_target target direction for the robot to face, field centric, bearing degrees
     * @param i_gyro the gyro value, field centric, in bearing degrees
     * @return double that indicates what the rotational joystick value should be
     */
    public double getRotControl(double i_target, double i_gyro){
        rotDelta = i_target - i_gyro;
        if (rotDelta > 180){
            rotPID = (rotDelta-360)/180;
        } else if (Math.abs(rotDelta) <= 180.0){
            rotPID = rotDelta/180.0;
        } else {
            rotPID = (rotDelta+360)/180;
        } 
        return rotPID;
    }

    /**determines the translational magnitude of the robot in autonomous
     * 
     * @param pathPos path data for position of the robot, inches
     * @param pathVel path data for velocity of the robot, inches
     * @param distTravelled distance the robot has travelled, inches
     * @return double for magnitude of translational vector
     */
    public double getAutoPower(double pathPos, double pathVel, double distTravelled){
        double guess = pathVel * DriveConstants.DRIVE_F;
        double check = DriveConstants.DRIVE_P * (pathPos - distTravelled);
        return -(guess + check);
    }

    /**returns magnitude of vector components */
    private double getMagnitude(double x, double y){
        return Math.hypot(x, y);
    }
    /**x,y inputs are cartesian, angle values are in bearing, returns 0 - 360 */
    private double getDirection(double x, double y, double angle){
        double measurement =  Math.toDegrees(Math.atan2(x,y));//returns angle in bearing form
        if (measurement<0) measurement = 360+measurement;
        measurement = measurement - angle;
        if (measurement<0) measurement = 360+measurement;
        return measurement;
    }
    
}
