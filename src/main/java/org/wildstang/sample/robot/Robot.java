package org.wildstang.sample.robot;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.logger.Log;
import org.wildstang.framework.logger.Log.LogLevel;
import org.wildstang.hardware.roborio.RoboRIOInputFactory;
import org.wildstang.hardware.roborio.RoboRIOOutputFactory;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Once the Main class starts this class, the VM is configured to call the
 * functions corresponding to each mode, as described in the IterativeRobotBase
 * documentation. In summary, this class is the root of everything that the
 * robot does. Everytime the driver station or FMS tells the robot to enter a
 * new mode, this class handles that instruction.
 * 
 * In general, this class should not be modified.
 * 
 * @see <a href="https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/IterativeRobotBase.html">IterativeRobotBase</a>
 */
public class Robot extends TimedRobot {

    Core core;
    private SendableChooser<LogLevel> logChooser;

    /**
     * Runs on initialization, creates and configures the framework Core.
     */
    @Override
    public void robotInit() {
        Log.info("Initializing robot.");

        core = new Core(RoboRIOInputFactory.class, RoboRIOOutputFactory.class);
        core.createInputs(WsInputs.values());
        core.createOutputs(WsOutputs.values());
        core.createSubsystems(WsSubsystems.values());
        core.createAutoPrograms(WsAutoPrograms.values());
        
        // create smart dashboard option for LogLevel
        logChooser = new SendableChooser<>();
        logChooser.addOption(LogLevel.INFO.toString(), LogLevel.INFO);
        logChooser.setDefaultOption(LogLevel.WARN.toString(), LogLevel.WARN);
        logChooser.addOption(LogLevel.ERROR.toString(), LogLevel.ERROR);
        logChooser.addOption(LogLevel.NONE.toString(), LogLevel.NONE);
        SmartDashboard.putData("Log Level", logChooser);

        DataLogManager.start();
        // Record both DS control and joystick data
        DriverStation.startDataLog(DataLogManager.getLog());

    }

    /**
     * Runs when robot is disabled, announces that the robot has been disabled.
     */
    @Override
    public void disabledInit() {
        Log.info("Engaging disabled mode.");
    }

    /**
     * Runs when autonomous is enabled. Resets all the subsystems, then starts
     * the currently selected (and locked in) auto program.
     */
    @Override
    public void autonomousInit() {
        Log.danger("Engaging autonomous mode.");
        Core.getSubsystemManager().resetState();
        Core.getAutoManager().startCurrentProgram();
        
    }

    /**
     * Runs when teleoperated is enabled.
     * Cleans up from autonomous then resets all the subsystems.
     * Note: once teleopInit() is called auto cannot be run again.
     */
    @Override
    public void teleopInit() {
        Log.danger("Engaging teleoperated mode.");
        // tell AutoManager not to preload or run any more programs
        Core.getAutoManager().endPeriod();
        Core.getSubsystemManager().resetState();
        Core.setToTeleop();
    
    }

    /**
     * Runs when test is enabled. Currently does nothing.
     * @see <a href="https://github.com/wildstang/robot_framework/issues/10">robot_framework#10</a>
     */
    @Override
    public void testInit() {
        Log.danger("Engaging test mode.");
    }

    /**
     * Runs repeatedly, regardless of mode and state.
     * Tells the core to run each manager.
     * @see <a href="https://github.com/wildstang/robot_framework/issues/26">robot_framework#26</a>
     */
    @Override
    public void robotPeriodic() {
        core.executeUpdate();
    }

    /**
     * Runs repeatedly while robot is disabled. Currently does nothing.
     */
    @Override
    public void disabledPeriodic() {
    }

    /**
     * Runs repeatedly while autonomous is enabled.
     * Tells the core to run each manager.
     */
    @Override
    public void autonomousPeriodic() {
        update();
    }

    /**
     * Runs repeatedly while teleoperated is enabled.
     * Tells the core to run each manager.
     */
    @Override
    public void teleopPeriodic() {
        update();
        
    }

    /**
     * Tell the core to run each manager; display statistics on the dashboard.
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
     * Runs repeatedly while test is enabled. Currently does nothing.
     * @see <a href="https://github.com/wildstang/robot_framework/issues/10">robot_framework#10</a>
     */
    @Override
    public void testPeriodic() {
    }
}