package org.wildstang.framework.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static Logger s_log = Logger.getLogger(AutoManager.class.getName());

    private List<AutoProgram> programs = new ArrayList<>();
    private AutoProgram runningProgram;
    private boolean programFinished;
    private SendableChooser<AutoProgram> chooser;
    private SendableChooser<Boolean> lockinChooser;

    /**
     * Loads in AutoPrograms and adds selectors to the SmartDashboard.
     */
    public AutoManager() {
        chooser = new SendableChooser<>();
        lockinChooser = new SendableChooser<>();
        lockinChooser.setDefaultOption("Unlocked", false);
        lockinChooser.addOption("Locked", true);

        defineDefaultPrograms();

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
        runningProgram.update();
        if (runningProgram.isFinished()) {
            programFinished = true;
        }
    }

    /**
     * Starts the currently selected program from SmartDashboard if locked in.
     */
    public void startCurrentProgram() {
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
        SmartDashboard.putString("Running Autonomous Program", program.toString());
    }

    /**
     * Resets the current program and all states.
     */
    public void init() {
        programFinished = false;
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
}
