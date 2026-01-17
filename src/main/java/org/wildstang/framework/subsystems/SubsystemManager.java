package org.wildstang.framework.subsystems;

import java.util.ArrayList;
import java.util.List;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.core.Subsystems;
import org.wildstang.framework.logger.Log;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class in the manager for all outputs.
 *
 * @author Steve
 */
public class SubsystemManager {

    private ArrayList<Subsystem> m_subsystems = new ArrayList<>();
    private boolean s_initialised = false;

    /**
     * Initialize all subsystems registered with the manager.
     */
    public void init() {
        if (!s_initialised) {
            s_initialised = true;
        }

        for (Subsystem sub : m_subsystems) {
            // Init all subsystems.
            sub.init();
        }
    }

    /**
     * Updates all subsystems registered with the manager.
     */
    public void update() {
        // Iterate over all outputs and update each one
        for (Subsystem sub : m_subsystems) {
            double startTime = Timer.getFPGATimestamp();
            // Update the output - send value to output
            sub.update();
            SmartDashboard.putNumber(sub.getName() + " time", Timer.getFPGATimestamp() - startTime);
        }
    }

    /**
     * Resets states of all subsystems registered with the manager.
     */
    public void resetState() {
        // Iterate over all outputs and update each one
        for (Subsystem sub : m_subsystems) {
            // Update the output - send value to output
            sub.resetState();
        }
    }

    /**
     * Registers a given subsystem with the manager.
     * @param p_subsystem Subsystem to register.
     */
    public void addSubsystem(Subsystem p_subsystem) {
        CoreUtils.checkNotNull(p_subsystem, "p_subsystem is null");

        if (!m_subsystems.contains(p_subsystem)) {
            m_subsystems.add(p_subsystem);
        }
    }

    /**
     * Deregisters a given subsystem from the manager.
     * @param p_subsystem Subsystem to deregister.
     */
    public void removeSubsystem(Subsystem p_subsystem) {
        CoreUtils.checkNotNull(p_subsystem, "p_subsystem is null");
        Log.info("Removing subsystem " + p_subsystem.getName());
        m_subsystems.remove(p_subsystem);
    }

    /**
     * Gets a given subsystem by name from the manager.
     * @param p_name Name of subsystem to get.
     * @return The registered system requested.
     */
    public Subsystem getSubsystem(String p_name) {
        CoreUtils.checkNotNull(p_name, "p_name is null");

        Subsystem result = null;

        for (Subsystem sub : m_subsystems) {
            if (sub.getName().equals(p_name)) {
                result = sub;
            }
        }

        return result;
    }

    /**
     * Gets a given subsystem from the manager.
     * @param desiredSubsystem Subsystem to get.
     * @return The registered system requested.
     */
    public Subsystem getSubsystem(Subsystems desiredSubsystem) {
        return getSubsystem(desiredSubsystem.getName());
    }

    /**
     * Gets the liset of registered subsystems.
     * @return List of registered subsystems.
     */
    public List<Subsystem> getSubsystems() {
        return m_subsystems;
    }

    /**
     * Tests all subsystems registered with the manager.
     */
    public void selfTestAll() {
        for (Subsystem s : m_subsystems) {
            s.selfTest();
        }
    }

    /**
     * Deregisters all subsystems from the manager.
     */
    public void removeAll() {
        m_subsystems.clear();
    }

    /**
     * Gets the size of all subsystems registered with the manager.
     * @return Size of subsystems array.
     */
    public int size() {
        return m_subsystems.size();
    }
}
