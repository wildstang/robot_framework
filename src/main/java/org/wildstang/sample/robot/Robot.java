package org.wildstang.sample.robot;

import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.rlog.RLOGServer;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;
import org.littletonrobotics.urcl.URCL;
import org.wildstang.framework.core.Core;
import org.wildstang.framework.logger.Log;
import org.wildstang.framework.logger.Log.LogLevel;
import org.wildstang.hardware.roborio.RoboRIOInputFactory;
import org.wildstang.hardware.roborio.RoboRIOOutputFactory;

import au.grapplerobotics.CanBridge;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
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
public class Robot extends LoggedRobot {

    Core core;
    private SendableChooser<LogLevel> logChooser;

    public Robot(){
        // Set up data receivers & replay source
        switch (Core.currentMode) {
            case REAL:
            // Running on a real robot, log to NT, which will be logged to robot by DataLogManager
            Logger.addDataReceiver(new NT4Publisher());
            break;
    
            case SIM:
            // Running a physics simulator, log to NT
            Logger.addDataReceiver(new NT4Publisher());
            break;
    
            case REPLAY:
            // Replaying a log, set up replay source
            setUseTiming(false); // Run as fast as possible
            String logPath = LogFileUtil.findReplayLog();
            Logger.setReplaySource(new WPILOGReader(logPath));
            Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim")));
            break;
        }
        Logger.registerURCL(URCL.startExternal(CANConstants.aliasMap));

        CanBridge.runTCP();
    }
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
        //DriverStation.startDataLog(DataLogManager.getLog());
        Logger.start();
    }

    /**
     * Runs when robot is disabled, announces that the robot has been disabled.
     */
    @Override
    public void disabledInit() {
        Log.info("Engaging disabled mode.");
        Core.setIsDisabledMode(true);
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
        Core.setIsDisabledMode(false);
        
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
        Core.setIsDisabledMode(false);
    
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
        update();
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
        //update();
    }

    /**
     * Runs repeatedly while teleoperated is enabled.
     * Tells the core to run each manager.
     */
    @Override
    public void teleopPeriodic() {
        //update();
        
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