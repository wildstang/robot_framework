package org.wildstang.framework.auto;

/**
 * Represents a single automated robot operation.
 * When it's the step's turn, initialize() is run once,
 * then update() is run until setFinished(true).
 * Step will be initialized and the update method will be called for the first time in the same cycle
 */
public abstract class AutoStep {

    private boolean finished;

    /**
     * Set step to not finished.
     * Constructing does not start step, rather prepares it for initialization.
     */
    public AutoStep() {
        finished = false;
    }

    /**
     * This method is called once, when the step is first run.
     * Use this method to set up anything that is necessary for the step.
     */
    public abstract void initialize();
    
    /**
     * This method is called on the active step, once per call to RobotTemplate.autonomousPeriodic().
     * Steps will continue to have this method called until they set finished to true.
     * Note: this method is first called right after initialize(), with no delay in between.
     */
    public abstract void update();

    /**
     * Returns if the AutoStep is finished, meaning the next step may begin.
     * @return True if the step is finished.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Do not use this function in new implementations.
     * Previously setFinished took a boolean,
     * but there is not case where setFinished(false) is a valid operation.
     * This exists to allow it to still be called that way. 
     * @param isFinished Unused, finished is always set to true.
     */
    public void setFinished(boolean isFinished) {
        setFinished();
    }

    /**
     * Completes the set by setting finished to true.
     */
    public void setFinished() {
        finished = true;
    }

    /**
     * Returns the name of the AutoStep, used to uniquely identify the step.
     * @return Name of the AutoStep.
     */
    @Override
    public abstract String toString();
}
