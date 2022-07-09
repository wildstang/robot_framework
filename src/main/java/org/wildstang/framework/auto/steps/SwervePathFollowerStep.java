package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.subsystems.swerve.SwerveDriveTemplate;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    private double lastPos;

    private Timer timer;

    /** Sets the robot to track a new path
     * finishes after all values have been read to robot
     * @param pathData double[][] that contains path, should be from \frc\paths
     * @param drive the swerveDrive subsystem
     */
    public SwervePathFollowerStep(double[][] pathData, SwerveDriveTemplate drive) {
        this.pathData = pathData;
        m_drive = drive;
        timer = new Timer();
    }

    @Override
    public void initialize() {
        //start path
        counter = 0;
        lastPos = 0;
        m_drive.resetDriveEncoders();
        m_drive.setToAuto();
        timer.start();
    }

    @Override
    public void update() {
        if (counter >= pathData.length) {
            setFinished();
        } else {
            SmartDashboard.putNumber("Auto Time", counter*0.02);
            SmartDashboard.putNumber("Auto Attempted position", pathData[counter][positionP]);
            if (lastPos == pathData[counter][positionP]){
                //end path
                m_drive.setAutoValues(pathData[counter][positionP]*ftToIn, 0, -Math.toDegrees(pathData[counter][headingP]));
                counter = pathData.length;
            } else if (timer.get()>=0.02){
                //update values the robot is tracking to
                System.out.println("Target position: " + pathData[counter][positionP]*ftToIn);
                m_drive.setAutoValues(pathData[counter][positionP]*ftToIn, ((pathData[counter][positionP] - lastPos)/0.02)*ftToIn, -Math.toDegrees(pathData[counter][headingP]));
                lastPos = pathData[counter][positionP];
                counter++;
                timer.advanceIfElapsed(0.02);
            }
        }
        //System.out.println("Time: " + timer.get() + ", counter " + (counter-1));
    }

    @Override
    public String toString() {
        return "Swerve Path Follower";
    }

}
