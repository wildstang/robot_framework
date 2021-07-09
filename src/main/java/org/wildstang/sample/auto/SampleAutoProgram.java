package org.wildstang.sample.auto;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.auto.steps.control.AutoStepDelay;

/**
 * Sample auto program that just waits 10 seconds before finishing.
 * @author Liam
 */
public class SampleAutoProgram extends AutoProgram {

    @Override
    protected void defineSteps() {
        addStep(new AutoStepDelay(10));
    }

    @Override
    public String toString() {
        return "Sample";
    }
    
}