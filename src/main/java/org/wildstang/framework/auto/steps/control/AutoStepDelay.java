package org.wildstang.framework.auto.steps.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wildstang.framework.auto.AutoStep;

import edu.wpi.first.wpilibj.Timer;

/**
 * This step delays testing for the specified number of cycles. Note: If
 * used in a parallel step group, it insures that the group waits for at
 * least the specified number of cycles, instead.
 * @author coder65535
 */
public class AutoStepDelay extends AutoStep {
    
    private static Logger s_log = Logger.getLogger(AutoStepDelay.class.getName());

    protected final double delay;
    protected Timer timer;

    /**
     * Constructing creates a new timer for msDelay ms.
     * @param msDelay Number of milliseconds to wait before finishing.
     */
    public AutoStepDelay(int msDelay) {
        this.delay = msDelay / 1000.0;
        this.timer = new Timer();
        if (msDelay < 0) {
            s_log.logp(Level.FINE, this.getClass().getName(), "AutonomousStepDelay",
                    "Delay must be greater than 0");
        }
    }

    /**
     * Starts the timer on initialization.
     */
    @Override
    public void initialize() {
        timer.reset();
        timer.start();
    }

    /**
     * Once timer has passed the AutoStep finishes.
     */
    @Override
    public void update() {
        if (timer.hasPeriodPassed(delay)) {
            timer.stop();
            setFinished(true);
        }
    }

    /**
     * Returns the name of the AutoStep, "Delay for X ms".
     * @return Name of the AutoStep.
     */
    @Override
    public String toString() {
        return "Delay for " + (int)(delay * 1000) + " ms (" + (int)((10 - timer.get()) * 1000) + " ms remaining)";
    }
}
