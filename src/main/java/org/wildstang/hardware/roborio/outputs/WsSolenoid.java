package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.DigitalOutput;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Controls a single solenoid.
 */
public class WsSolenoid extends DigitalOutput {

    Solenoid solenoid;

    /**
     * Constructs the solenoid from config.
     * @param name Descriptive name of the solenoid.
     * @param module Model of PCM used which the solenoid is connected to.
     * @param channel1 Hardware port number the solenoid is connected to.
     * @param p_default Default state.
     */
    public WsSolenoid(String name, PneumaticsModuleType module, int channel1, boolean p_default) {
        super(name, p_default);

        solenoid = new Solenoid(module, channel1);
        solenoid.set(p_default);
    }

    /**
     * Constructs the solenoid from config.
     * This constructor is used if the CAN ID of the PCM is not the default value.
     * @param name Descriptive name of the solenoid.
     * @param canId CAN id of the PCM the solenoid is connected to.
     * @param module Model of PCM used which the solenoid is connected to.
     * @param channel1 Hardware port number the solenoid is connected to.
     * @param p_default Default state.
     */
    public WsSolenoid(String name, int canId, PneumaticsModuleType module, int channel1, boolean p_default) {
        super(name, p_default);

        solenoid = new Solenoid(canId, module, channel1);
        solenoid.set(p_default);
    }

    /**
     * Constructs the solenoid from config.
     * @param name Descriptive name of the solenoid.
     * @param channel1 Hardware port number the solenoid is connected to.
     */
    public WsSolenoid(String name, int channel1) {
        this(name, PneumaticsModuleType.REVPH, channel1, false);
    }

    /**
     * Sets solenoid state to current value.
     */
    @Override
    public void sendDataToOutput() {
        solenoid.set(getValue());
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }
}
