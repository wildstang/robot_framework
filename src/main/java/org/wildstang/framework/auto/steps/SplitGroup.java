package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;

public class SplitGroup{

    AutoStep mainStep;
    AutoStep[] steps;
    AutoParallelStepGroup parallel = new AutoParallelStepGroup();
    AutoSerialStepGroup serial = new AutoSerialStepGroup();

    public SplitGroup(AutoStep step, AutoStep[] otherSteps){
        this.mainStep = step;
        this.steps = otherSteps;
        AutoParallelStepGroup parallel = new AutoParallelStepGroup();
        AutoSerialStepGroup serial = new AutoSerialStepGroup();
        parallel.addStep(mainStep);
        for (int i = 0; i < steps.length; i++){
            serial.addStep(steps[i]);
        }
        parallel.addStep(serial);
    }
    public AutoStep get(){
        return parallel;
    }

    public String toString() {
        return "Split Group";
    }
    
}
