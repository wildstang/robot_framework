package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.DiscreteOutput;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

/**
 * Controls a double solenoid.
 */
public class WsDoubleSolenoid extends DiscreteOutput {

    DoubleSolenoid solenoid;

    /**
     * Constructs the solenoid from config.
     * @param name Descriptive name of the solenoid.
     * @param module Model of PCM used which the solenoid is connected to.
     * @param channel1 Hardware port number the first solenoid is connected to.
     * @param channel2 Hardware port number the second solenoid is connected to.
     * @param p_default Default state.
     */
    public WsDoubleSolenoid(String name, PneumaticsModuleType module, int channel1, int channel2, WsDoubleSolenoidState p_default) {
        super(name, p_default.ordinal());

        solenoid = new DoubleSolenoid(module, channel1, channel2);
    }

    /**
     * Constructs the solenoid from config.
     * This constructor is used if the CAN ID of the PCM is not the default value.
     * @param name Descriptive name of the solenoid.
     * @param canId CAN id of the PCM the solenoid is connected to.
     * @param module Model of PCM used which the solenoid is connected to.
     * @param channel1 Hardware port number the first solenoid is connected to.
     * @param channel2 Hardware port number the second solenoid is connected to.
     * @param p_default Default state.
     */
    public WsDoubleSolenoid(String name, int canId, PneumaticsModuleType module, int channel1, int channel2, WsDoubleSolenoidState p_default) {
        super(name, p_default.ordinal());

        solenoid = new DoubleSolenoid(canId, module, channel1, channel2);
    }

    /**
     * Sets solenoid state to current value.
     */
    @Override
    protected void sendDataToOutput() {
        DoubleSolenoid.Value solValue = DoubleSolenoid.Value.kOff;

        if (getValue() == WsDoubleSolenoidState.FORWARD.ordinal()) {
            solValue = DoubleSolenoid.Value.kForward;
        } else if (getValue() == WsDoubleSolenoidState.REVERSE.ordinal()) {
            solValue = DoubleSolenoid.Value.kReverse;
        } else if (getValue() == WsDoubleSolenoidState.OFF.ordinal()) {
            solValue = DoubleSolenoid.Value.kOff;
        }

        solenoid.set(solValue);
    }

}
