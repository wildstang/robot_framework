package org.wildstang.sample.robot;

import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Subsystems;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.sample.subsystems.targeting.WsVision;

/**
 * All subsystems are enumerated here.
 * It is used in Robot.java to initialize all subsystems.
 */
public enum WsSubsystems implements Subsystems {

    // enumerate subsystems
    WS_VISION(WsVision.NAME, WsVision.class),
    SWERVE_DRIVE(SwerveDrive.NAME, SwerveDrive.class)
    ;

    /**
     * Do not modify below code, provides template for enumerations.
     * We would like to have a super class for this structure, however,
     * Java does not support enums extending classes.
     */

    private String name;
    private Class<? extends Subsystem> subsystemClass;

    /**
     * Initialize name and Subsystem map.
     * 
     * @param name Name, must match that in class to prevent errors.
     * @param subsystemClass Class containing Subsystem
     */
    WsSubsystems(String name, Class<? extends Subsystem> subsystemClass) {
        this.name = name;
        this.subsystemClass = subsystemClass;
    }

    /**
     * Returns the name mapped to the subsystem.
     * 
     * @return Name mapped to the subsystem.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns subsystem's class.
     * 
     * @return Subsystem's class.
     */
    @Override
    public Class<? extends Subsystem> getSubsystemClass() {
        return subsystemClass;
    }

    /**
     * Returns the actual Subsystem object from the SubsystemManager
     * 
     * @return The corresponding subsystem.
     */
    public Subsystem get() {
        return Core.getSubsystemManager().getSubsystem(this);
    }
}