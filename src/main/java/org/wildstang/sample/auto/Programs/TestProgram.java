package org.wildstang.sample.auto.Programs;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.PathHeadingStep;
import org.wildstang.framework.auto.steps.SetGyroStep;
import org.wildstang.framework.auto.steps.SwervePathFollowerStep;
import org.wildstang.sample.auto.Steps.StartOdometryStep;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;

/**
 * A basic path-following step that follows the "Test_100" path.
 */
public class TestProgram extends AutoProgram {

    public static final String NAME = "Test Program";

    @Override
    protected void defineSteps() {
        // access the swerve drive
        SwerveDrive swerve = (SwerveDrive) WsSubsystems.SWERVE_DRIVE.get();

        // calibrate the gyro to 180 degrees
        addStep(new SetGyroStep(180.0, swerve));

        // turn the robot 180 degrees
        addStep(new PathHeadingStep(180.0, swerve));

        // tell the robot it is at (3, 5), 180 degrees, and on the blue alliance
        addStep(new StartOdometryStep(3.0, 5.0, 180.0, true));

        // follow the "Test_100" path
        PathConstraints constraints = new PathConstraints(4, 3);
        PathPlannerTrajectory path = PathPlanner.loadPath("Test_100", constraints);
        addStep(new SwervePathFollowerStep(path, swerve, true));
    }

    @Override
    public String toString() {
        return NAME;
    }
}
