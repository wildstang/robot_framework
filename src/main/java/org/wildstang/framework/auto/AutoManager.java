package org.wildstang.framework.auto;

import java.util.ArrayList;
import java.util.List;

import org.wildstang.framework.auto.program.Sleeper;
import org.wildstang.framework.logger.Log;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Manages all autonomous and AutoPrograms for the framework.
 * Note: This manager previously tracked an AutoStartPosition Enum
 * which was used to compare the current position to where the robot
 * started autonomous. Due to a lack of use this was removed.
 * @author Nathan
 */
public class AutoManager {

    private List<AutoProgram> programs = new ArrayList<>();
    private AutoProgram runningProgram;
    private boolean programFinished;
    private SendableChooser<AutoProgram> chooser;
    private SendableChooser<Boolean> lockinChooser;
    private boolean hasStarted;
    private boolean lastLock;

    /**
     * Loads in AutoPrograms and adds selectors to the SmartDashboard.
     */
    public AutoManager() {
        chooser = new SendableChooser<>();
        lockinChooser = new SendableChooser<>();
        lockinChooser.setDefaultOption("Unlocked", false);
        lockinChooser.addOption("Locked", true);

        defineDefaultPrograms();
        chooser.setDefaultOption(programs.get(0).toString(), programs.get(0));

        SmartDashboard.putData("Select Autonomous Program", chooser);
        SmartDashboard.putData("Lock in auto program", lockinChooser);
    }

    /**
     * Updates the running AutoProgram and detects if it is finished.
     */
    public void update() {
        if (programFinished) {
            runningProgram.cleanup();
            programFinished = false;
            startSleeper();
        }
        // if an auto program hasn't started yet and only once after the lock state changes
        else if (!hasStarted) {
            boolean locked = lockinChooser.getSelected();
            if (locked != lastLock) {
                Log.info("Lock state changed to: " + locked);
                lastLock = locked;
                // attempt to preload the selected program
                preloadLockedProgram();
            }
        }

        // display currently loading and running program
        if (runningProgram != null && runningProgram.areStepsDefined()) {
            SmartDashboard.putString("Preloaded Autonomous Program", runningProgram.toString());
        }
        else {
            SmartDashboard.putString("Preloaded Autonomous Program", "");
        }
        if (runningProgram != null && hasStarted) {
            SmartDashboard.putString("Running Autonomous Program", runningProgram.toString());
        }
        else {
            SmartDashboard.putString("Running Autonomous Program", "");
        }

        runningProgram.update();
        if (runningProgram.isFinished()) {
            programFinished = true;
        }
    }

    /**
     * Preloads the currently selected program from SmartDashboard if locked in.
     */
    public void preloadLockedProgram() {
        if (lockinChooser.getSelected()) {
            preloadProgram(chooser.getSelected());
        }
    }

    /**
     * Defines steps for the given program.
     * @param program New program to preload.
     */
    private void preloadProgram(AutoProgram program) {
        if (runningProgram != null && runningProgram.areStepsDefined()) {
            // if this is a new program, clear out old program's steps
            if (runningProgram != program) {
                runningProgram.cleanup();
            }
            else {
                Log.warn("Steps already defined for " + program.toString());
                return;
            }
        }

        if (program != null) {
            runningProgram = program;
            program.defineSteps();
            Log.info("Preloading auto program: " + program.toString());
        }
    }

    /**
     * Starts the currently selected program from SmartDashboard if locked in.
     */
    public void startCurrentProgram() {
        hasStarted = true;
        if (lockinChooser.getSelected()) {
            startProgram(chooser.getSelected());
        } else {
            startSleeper();
        }
    }

    /**
     * Starts the default program, Sleeper, which does nothing and never ends.
     */
    public void startSleeper() {
        startProgram(programs.get(0));
    }

    /**
     * Starts a given AutoProgram and announces it at Info level and on dashboard.
     * @param program New program to run.
     */
    private void startProgram(AutoProgram program) {
        runningProgram = program;
        program.initialize();
        Log.info("Starting auto program: " + program.toString());
    }

    /**
     * Resets the current program and all states.
     */
    public void init() {
        programFinished = false;
        hasStarted = false;
        lastLock = false;
        if (runningProgram != null) {
            runningProgram.cleanup();
        }
        runningProgram = programs.get(0);
        SmartDashboard.putString("Running Autonomous Program", "No Program Running");
    }

    /**
     * Returns the current running AutoProgram, if one is running.
     * @return The current running program, or null.
     */
    public AutoProgram getRunningProgram() {
        if (runningProgram != null && !programFinished) {
            return runningProgram;
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the current running AutoProgram, if one is running.
     * @return The name of the running program, or an empty String.
     */
    public String getRunningProgramName() {
        AutoProgram p = getRunningProgram();
        if (p != null) {
            return p.toString();
        }
        else {
            return "";
        }
    }

    /**
     * Defines the default set of AutoPrograms.
     * The first should always be Sleeper, which does nothing and never finishes.
     * Do not change that.
     */
    private void defineDefaultPrograms() {
        addProgram(new Sleeper());
    }

    /**
     * Adds a new program to AutoManager and SmartDashboard chooser.
     * @param program New AutoProgram to add.
     */
    public void addProgram(AutoProgram program) {
        programs.add(program);
        chooser.addOption(program.toString(), program);
    }

    /**
     * Ends the autonomous period, prevents it from continuing into tele.
     */
    public void endPeriod() {
        // prevent any more programs from being preloaded
        hasStarted = true;

        // tell all programs to clear out their steps
        for (AutoProgram program : programs) {
            program.cleanup();
        }

        // trigger Sleeper program on next update
        programFinished = true;
    }

    public boolean isAutoLocked(){
        return lockinChooser.getSelected();
    }
}
