package org.wildstang.sample.robot;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.AutoPrograms;
import org.wildstang.sample.auto.Programs.TestProgram;

/**
 * All active AutoPrograms are enumerated here.
 * It is used in Robot.java to initialize all programs.
 */
public enum WsAutoPrograms implements AutoPrograms {

    // enumerate programs
    TEST_PROGRAM(TestProgram.NAME, TestProgram.class),
    ;

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */
    private String name;
    private Class<? extends AutoProgram> programClass;

    /**
     * Initialize name and AutoProgram map.
     * 
     * @param name Name, must match that in class to prevent errors.
     * @param programClass Class containing AutoProgram
     */
    WsAutoPrograms(String name, Class<? extends AutoProgram> programClass) {
        this.name = name;
        this.programClass = programClass;
    }

    /**
     * Returns the name mapped to the AutoProgram.
     * 
     * @return Name mapped to the AutoProgram.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns AutoProgram's class.
     * 
     * @return AutoProgram's class.
     */
    @Override
    public Class<? extends AutoProgram> getProgramClass() {
        return programClass;
    }
}