package org.wildstang.framework.subsystems;

import org.wildstang.framework.io.InputListener;

/**
 * Interface describing a subsystem class.
 */
public interface Subsystem extends InputListener {

    /**
     * Initialise the subsystem. Performs any required setup work.
     */
    public void init();

    /**
     * Can be called to reset any state variables. Can be used when changing modes
     * or reenabling system to reset to a default state without reinitialising
     * connected components. Called after init().
     */
    public void resetState();

    /**
     * Called to cause the subsystem to update its state and set new values on
     * outputs.
     *
     * Sending values to the hardware outputs is done outside of this method by the
     * framework.
     */
    public void update();

    /**
     * Returns the name of the subsystem.
     *
     * @return the name of the subsystem
     */
    public String getName();

    /**
     * Performs a self test of the subsystem.
     */
    public void selfTest();

}
