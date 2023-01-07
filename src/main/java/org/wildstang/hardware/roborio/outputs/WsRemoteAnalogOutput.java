package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.AnalogOutput;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Controls a remote analog output.
 */
public class WsRemoteAnalogOutput extends AnalogOutput {

    DoublePublisher publisher;

    /**
     * Constructs the remote output from config.
     * @param name Descriptive name of the remote output.
     * @param p_networkTbl Network table name.
     * @param p_default Default value.
     */
    public WsRemoteAnalogOutput(String name, String p_networkTbl, double p_default) {
        super(name, p_default);
        NetworkTable remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);
        publisher = remoteIOTable.getDoubleTopic(name).publish();
    }

    /**
     * Sets remote output state to current value.
     */
    @Override
    protected void sendDataToOutput() {
        publisher.set(getValue());
    }

    /**
     * Does nothing, config values only affects start state.
     */
    public void notifyConfigChange() { }

}