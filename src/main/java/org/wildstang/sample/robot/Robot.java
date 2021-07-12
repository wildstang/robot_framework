package org.wildstang.sample.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.logger.Log;
import org.wildstang.framework.logger.Log.LogLevel;
import org.wildstang.hardware.roborio.RoboRIOInputFactory;
import org.wildstang.hardware.roborio.RoboRIOOutputFactory;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {

    Core core;
    private boolean AutoFirstRun = true;
    private SendableChooser<LogLevel> logChooser;

    /**
     * Runs on initialization, creates and configure framework Core.
     */
    @Override
    public void robotInit() {
        Log.info("Initializing robot.");

        core = new Core(RoboRIOInputFactory.class, RoboRIOOutputFactory.class);
        core.createInputs(WSInputs.values());
        core.createOutputs(WSOutputs.values());
        core.createSubsystems(WSSubsystems.values());
        core.createAutoPrograms(WSAutoPrograms.values());
        
        // create smart dashboard option for LogLevel
        logChooser = new SendableChooser<>();
        logChooser.addOption(LogLevel.INFO.toString(), LogLevel.INFO);
        logChooser.setDefaultOption(LogLevel.WARN.toString(), LogLevel.WARN);
        logChooser.addOption(LogLevel.ERROR.toString(), LogLevel.ERROR);
        logChooser.addOption(LogLevel.NONE.toString(), LogLevel.NONE);
        SmartDashboard.putData("Log Level", logChooser);
    }

    /**
     * Runs when robot is disabled.
     */
    @Override
    public void disabledInit() {
        Log.info("Engaging disabled mode.");
    }

    /**
     * Runs when autonomous is enabled.
     */
    @Override
    public void autonomousInit() {
        Log.danger("Engaging autonomous mode.");
        Core.getSubsystemManager().resetState();
        Core.getAutoManager().startCurrentProgram();
    }

    /**
     * Runs when teleoperated is enabled.
     */
    @Override
    public void teleopInit() {
        Log.danger("Engaging teleoperated mode.");
        Core.getSubsystemManager().resetState();
    }

    /**
     * Runs when test is enabled.
     */
    @Override
    public void testInit() {
        Log.danger("Engaging test mode.");
    }

    /**
     * Runs repeatedly, regardless of mode and state.
     */
    @Override
    public void robotPeriodic() {
        core.executeUpdate();

        // Empty the state tracker so we don't OOM out
        // TODO: figure out what this thing is and why
        Core.getStateTracker().getStateList();
    }

    /**
     * Runs repeatedly while robot is disabled.
     */
    @Override
    public void disabledPeriodic() {
        resetRobotState();
    }
    
    /**
     * Utility functions to reset if auto has run when disabled.
     */
    private void resetRobotState() {
        AutoFirstRun = true;
    }

    /**
     * Runs repeatedly while autonomous is enabled.
     */
    @Override
    public void autonomousPeriodic() {
        update();

        if (AutoFirstRun) {
            AutoFirstRun = false;
        }
    }

    /**
     * Runs repeatedly while teleoperated is enabled.
     */
    @Override
    public void teleopPeriodic() {
        update();
    }

    /**
     * Run core update and display statistics on dashboard.
     */
    private void update() {
        // update log level from chooser
        Log.setLevel(logChooser.getSelected());
        try {
            // Update all inputs, outputs and subsystems
            long start = System.currentTimeMillis();
            core.executeUpdate();
            long end = System.currentTimeMillis();
            SmartDashboard.putNumber("Cycle Time", (end - start));
        } catch (Throwable e) {
            SmartDashboard.putString("Last error", "Exception thrown during teleopPeriodic");
            SmartDashboard.putString("Exception thrown", e.toString());
            throw e;
        } finally {
            SmartDashboard.putBoolean("ExceptionThrown",true);
        }
    }

    /**
     * Runs repeatedly while test is enabled.
     */
    @Override
    public void testPeriodic() {
    }
}