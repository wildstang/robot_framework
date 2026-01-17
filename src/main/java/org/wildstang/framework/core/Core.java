package org.wildstang.framework.core;

import org.wildstang.framework.CoreUtils;
import org.wildstang.framework.auto.AutoManager;
import org.wildstang.framework.auto.AutoProgram;
import org.wildstang.framework.config.ConfigManager;
import org.wildstang.framework.hardware.InputFactory;
import org.wildstang.framework.hardware.OutputFactory;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.io.InputManager;
import org.wildstang.framework.io.outputs.Output;
import org.wildstang.framework.io.OutputManager;
import org.wildstang.framework.logger.Log;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.subsystems.SubsystemManager;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Core of robot framework.
 */
public class Core {

    private static InputManager s_inputManager;
    private static OutputManager s_outputManager;
    private static SubsystemManager s_subsystemManager;
    private static ConfigManager s_configManager;
    private static InputFactory s_inputFactory;
    private static OutputFactory s_outputFactory;
    private static AutoManager s_autoManager;

    private Class<?> m_inputFactoryClass;
    private Class<?> m_outputFactoryClass;
    private static Alliance s_alliance = Alliance.Blue;
    // Holds last value of network tables isBlue identify whether it has been edited
    private static boolean isBlueToggle = true;

    private static boolean isDisabled = true;

    public static final Mode simMode = Mode.SIM;
    public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

    /**
        * Defines the runtime mode used by AdvantageKit. The mode is always "real" when running
        * on a roboRIO. Change the value of "simMode" to switch between "sim" (physics sim) and "replay"
        * (log replay from a file).
    */
    public static enum Mode {
        /** Running on a real robot. */
        REAL,

        /** Running a physics simulator. */
        SIM,

        /** Replaying from a log file. */
        REPLAY
    }

    /**
     * Constructor collects I/O factory and initialized framework components.
     * @param p_inputFactoryClass InputFactory Class
     * @param p_outputFactoryClass OutputFactory Class
     */
    public Core(Class<?> p_inputFactoryClass, Class<?> p_outputFactoryClass) {
        CoreUtils.checkNotNull(p_inputFactoryClass, "p_inputFactoryClass is null");
        CoreUtils.checkNotNull(p_outputFactoryClass, "p_outputFactoryClass is null");

        m_inputFactoryClass = p_inputFactoryClass;
        m_outputFactoryClass = p_outputFactoryClass;

        init();
    }

    /**
     * Create all managers and factories for the subsystem.
     * The previous author suggested this shouldn't be run on construction.
     */
    private void init() {
        s_inputManager = new InputManager();
        s_inputManager.init();

        s_outputManager = new OutputManager();
        s_outputManager.init();

        s_subsystemManager = new SubsystemManager();
        s_subsystemManager.init();

        s_autoManager = new AutoManager();
        s_autoManager.init();

        s_configManager = new ConfigManager();
        s_configManager.init();

        s_inputFactory = (InputFactory) createObject(m_inputFactoryClass);
        s_inputFactory.init();

        s_outputFactory = (OutputFactory) createObject(m_outputFactoryClass);
        s_outputFactory.init();
    }

    /**
     * Uses RoboRIOOutputFactory to take an array of enumerations of Outputs and build an Output of each.
     * @param p_outputs All enumerations of Outputs to be created.
     */
    public void createOutputs(Outputs[] p_outputs) {
        Output output = null;

        // Iterate over all output enum values and create an output for each
        for (Outputs output_enum : p_outputs) {
            Log.info("Creating output: " + output_enum.getName());

            // Check if it is digital or analog, to create the correct type
            output = s_outputFactory.createOutput(output_enum);

            // Add the output to the output manager
            s_outputManager.addOutput(output);
        }
    }

    /**
     * Uses RoboRIOInputFactory to take an array of enumerations of Inputs and build an Input of each.
     * @param p_inputs All enumerations of Inputs to be created.
     */
    public void createInputs(Inputs[] p_inputs) {
        Input input = null;

        // Iterate over all input enum values and create an input for each
        for (Inputs input_enum : p_inputs) {
            Log.info("Creating input: " + input_enum.getName());

            input = s_inputFactory.createInput(input_enum);

            // Add the input to the input manager
            s_inputManager.addInput(input);
        }
    }

    /**
     * Takes an array of enumerations of Subsystems and build a Subsystem of each.
     * @param p_subsystems All enumerations of Subsystems to be created.
     */
    public void createSubsystems(Subsystems[] p_subsystems) {
        // Iterate over all input enum values and create a subsystem for each
        for (Subsystems subsystem : p_subsystems) {
            Log.info("Creating subsystem: " + subsystem.getName());

            // Instantiate the class
            Subsystem sub = (Subsystem) createObject(subsystem.getSubsystemClass());

            // Call the init method
            sub.init();

            s_subsystemManager.addSubsystem(sub);
        }
        for (Subsystems subsystem : p_subsystems) {
            Log.info("Creating subsystem: " + subsystem.getName());

            // Instantiate the class
            Subsystem sub = (Subsystem) s_subsystemManager.getSubsystem(subsystem);
            // Call the init method
            sub.initSubsystems();
        }
    }

    /**
     * Takes an array of enumerations of AutoPrograms and build an AutoProgram of each.
     * @param p_programs All enumerations of AutoPrograms to be created.
     */
    public void createAutoPrograms(AutoPrograms[] p_programs) {
        // Iterate over all input enum values and create a subsystem for each
        for (AutoPrograms program : p_programs) {
            Log.info("Creating auto program: " + program.getName());

            // Instantiate the class
            AutoProgram prog = (AutoProgram) createObject(program.getProgramClass());

            s_autoManager.addProgram(prog);
        }
    }

    public static Alliance getAlliance() {
        return s_alliance;
    }

    public static void setAlliance(Alliance alliance) {
        s_alliance = alliance;
    }

    public static Boolean isBlue() {
        return s_alliance == Alliance.Blue;
    }
    public static boolean getIsDisabledMode(){
        return isDisabled;
    }
    public static void setIsDisabledMode(boolean state){
        isDisabled = state;
    }
    public static boolean isAutoLocked(){
        return s_autoManager.isAutoLocked();
    }

    /**
     * Returns the framework's InputManager.
     * @return InputManager belonging to the framework.
     */
    public static InputManager getInputManager() {
        return s_inputManager;
    }

    /**
     * Returns the framework's OutputManager.
     * @return OutputManager belonging to the framework.
     */
    public static OutputManager getOutputManager() {
        return s_outputManager;
    }

    /**
     * Returns the framework's SubsystemManager.
     * @return SubsystemManager belonging to the framework.
     */
    public static SubsystemManager getSubsystemManager() {
        return s_subsystemManager;
    }

    /**
     * Returns the framework's AutoManager.
     * @return AutoManager belonging to the framework.
     */
    public static AutoManager getAutoManager() {
        return s_autoManager;
    }

    /**
     * Returns the framework's ConfigManager.
     * @return ConfigManager belonging to the framework.
     */
    public static ConfigManager getConfigManager() {
        return s_configManager;
    }

    /**
     * Creates an object from a Class object.
     * @param p_class Class to construct.
     * @return Constructed class.
     */
    protected Object createObject(Class<?> p_class) {
        CoreUtils.checkNotNull(p_class, "p_class is null");

        Object obj = null;

        try {
            obj = p_class.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Runs update function of all managers belonging to the framework.
     */
    public void executeUpdate() {
        if (SmartDashboard.getBoolean("isBlue", isBlueToggle) != isBlueToggle) {
            isBlueToggle = !isBlueToggle;
            setAlliance(isBlueToggle ? Alliance.Blue : Alliance.Red);
        }
        SmartDashboard.putBoolean("isBlue", isBlue());
        isBlueToggle = isBlue();



        // Read input from hardware
        s_inputManager.update();

        // Update the AutoPrograms, most of the time this is just sleeper
        s_autoManager.update();

        // Let subsystems react to changes
        s_subsystemManager.update();

        // Update outputs - send data to devices
        s_outputManager.update();
    }

}
