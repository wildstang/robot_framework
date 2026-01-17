package org.wildstang.sample.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;



public class SwerveMultiPointStep extends AutoStep {

    private SwerveDrive swerve;

    private int index = 0;
    private Pose2d[] poses;
    private double turnStartTime; // Time to start turning to the end heading


    private Timer timer = new Timer();

    /**
     * Drives through multiple points at full speed for intermediate points or limit given by speeds array
     * Uses P loop for final point
     * Threshold for atPosition for intermediate poses is lower
     * @param poses list of poses to drive through
     * @param speeds speeds limts to get to each pose
     * @param turnstart time to delay turning to the pose
     */
    public SwerveMultiPointStep(Pose2d[] poses, double[] speeds, double turnstart) {
        this.turnStartTime = turnstart;
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
        this.poses = poses;
    }

    public SwerveMultiPointStep(Pose2d[] poses, double[] speeds) {
        this(poses, speeds, 0.0);
    }

    @Override
    public void initialize() {
        swerve.setToAuto();
        timer.start();
    }

    @Override
    public void update() {

        swerve.setAutoValues(new Pose2d(poses[index].getTranslation(), swerve.odoAngle()));

        // Drive to intermediate point
        if (index < poses.length - 1) {
            swerve.usePID(false);
            if (swerve.isAtPosition(0.2)) {
                index++;
            }
        } else {
            swerve.usePID(true);
            if (swerve.isAtPosition()) {
                setFinished();
                return;
            }
            swerve.usePID(true);
        }
        if (timer.hasElapsed(turnStartTime)) {
            swerve.setAutoValues(poses[index]);
        } else {
            swerve.setAutoValues(new Pose2d(poses[index].getTranslation(), swerve.odoAngle()));
        }
    }

    @Override
    public String toString() {
        return "Swerve Multi Point Step";
    }
}
