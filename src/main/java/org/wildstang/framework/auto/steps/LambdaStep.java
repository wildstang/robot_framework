package org.wildstang.framework.auto.steps;

import org.wildstang.framework.auto.AutoStep;

public class LambdaStep extends AutoStep {

    // Function Interface
    public interface Procedure {
        void invoke();
    }

    private Procedure function;
    private String name;

    /**
     * Returns an AutoStep which runs a lambda function and then ends within the same cycle
     * Used to create AutoSteps which only need to call a function of a subsystem without writing a new class
     * Can be static fields of that subsystem
     * @param function Lambda function to run
     * @param name Name of the step published on Smart Dashboard
     */
    public LambdaStep(Procedure function, String name) {
        this.function = function;
        this.name = name;
    }

    @Override
    public void initialize() {
        function.invoke();
        this.setFinished();
    }

    @Override
    public void update() {
    }

    @Override
    public String toString() {
        return name;
    }
    
}
