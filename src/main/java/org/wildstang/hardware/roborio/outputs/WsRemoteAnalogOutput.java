package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Controls a remote analog output.
 */
public class WsRemoteAnalogOutput extends AnalogOutput {

    NetworkTable remoteIOTable;

    /**
     * Constructs the remote output from config.
     * @param name Descriptive name of the remote output.
     * @param p_networkTbl Network table name.
     * @param p_default Default value.
     */
    public WsRemoteAnalogOutput(String name, String p_networkTbl, double p_default) {
        super(name, p_default);
        remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);
    }

    /**
     * Sets remote output state to current value.
     */
    @Override
    protected void sendDataToOutput() {
        remoteIOTable.getEntry(getName()).setDouble(getValue());
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }

}