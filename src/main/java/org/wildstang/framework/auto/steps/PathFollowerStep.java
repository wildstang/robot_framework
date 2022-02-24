package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.framework.subsystems.drive.PathFollowingDrive;

public class PathFollowerStep extends AutoStep {

    private double[][] pathData;
    private PathFollowingDrive drive;
    private int iteration;

    public PathFollowerStep(double[][] pathInfo, PathFollowingDrive drive) {
        pathData = pathInfo;
        this.drive = drive;
        iteration = 0;
    }

    @Override
    public void initialize() {


        drive.setBrakeMode(true);
        drive.resetEncoders();
        drive.startPathFollower();
    }

    @Override
    public void update() {
        // keep running the PathFollower is active and not e-stopped
        if (!drive.isEstopActive() && iteration != pathData.length) {
            drive.updatePathFollower(pathData[iteration]);
            iteration++;
        }
        // otherwise cleanup and finish
        else {
            drive.stopPathFollower();
            setFinished(true);
        }
    }

    @Override
    public String toString() {
        return "Path Follower";
    }
    
}