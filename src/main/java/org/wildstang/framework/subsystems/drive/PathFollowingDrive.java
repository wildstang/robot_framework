package org.wildstang.framework.subsystems.drive;

import org.wildstang.framework.subsystems.Subsystem;

import edu.wpi.first.wpilibj.Notifier;

/**
 * Abstract representation of a Drive subsystem which support path following.
 */
public abstract class PathFollowingDrive implements Subsystem {
    
    private boolean estopActive = false;
    
    protected Notifier profileNotifier;

    /**
     * Enable brake or coast mode.
     * @param brake True to enable brake mode, false to enable coast.
     */
    public abstract void setBrakeMode(boolean brake);

    /**
     * Zero all the encoders on the drive base.
     */
    public abstract void resetEncoders();

    /**
     * Returns if the software emergency stop has been activated.
     * @return True if the emergency stop is active.
     */
    public boolean isEstopActive() {
        return estopActive;
    }
    
    /**
     * Initialize the motion profile and its notifier.
     * @param path Path to follow.
     * @param isForwards True if to follow the path forwards.
     */
    // public abstract void initPathFollower(Path path);

    /**
     * Enable the motion profile and start its notifier. 
     */
    public abstract void startPathFollower();

    /**
     * Update the motion profile. 
     * @param trajectoryInfo the line of double data for the current step of the path
     */
    public abstract void updatePathFollower(double[] trajectoryInfo);

    /**
     * Disable the motion profile and stop its notifier. 
     */
    public abstract void stopPathFollower();

    /**
     * Sets the gyro to the given value
     * @param degrees value you want the gyro to currently to be
     */
    public abstract void setGyro(double degrees);
}