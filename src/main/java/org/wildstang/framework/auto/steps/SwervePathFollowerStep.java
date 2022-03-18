package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

public class SwervePathFollowerStep extends AutoStep {

    private static final double ftToIn = 12;
    private static final int positionP = 7;
    private static final int velocityP = 8;
    private static final int headingP = 15;
    //dt, x, y, leftPos, leftVel, leftAcc, leftJer, centerPos, centerVel, centerAcc, centerJer, rightPos, rightVel, rightAcc, rightJer, heading
    //0   1  2    3          4       5        6          7          8         9          10        11         12       13        14       15

    private SwerveDriveTemplate m_drive;
    private double[][] pathData;

    private int counter;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     */
    public SwervePathFollowerStep(double[][] pathData, SwerveDriveTemplate drive) {
        this.pathData = pathData;
        m_drive = drive;
    }

    @Override
    public void initialize() {
        //start path
        counter = 0;
        m_drive.resetDriveEncoders();
    }

    @Override
    public void update() {
        if (counter >= pathData.length){
            //end path
            m_drive.stopMoving();
            setFinished(true);
        } else {
            //update values the robot is tracking to
            m_drive.setAutoValues(pathData[counter][positionP]*ftToIn, pathData[counter][velocityP]*ftToIn, Math.toDegrees(pathData[counter][headingP]));
            counter++;
        }
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

}
