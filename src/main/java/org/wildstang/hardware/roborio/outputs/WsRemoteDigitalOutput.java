package org.wildstang.hardware.roborio.outputs;

import org.wildstang.framework.io.outputs.DigitalOutput;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Controls a remote digital output.
 */
public class WsRemoteDigitalOutput extends DigitalOutput {

    BooleanPublisher publisher;

    /**
     * Constructs the remote output from config.
     * @param name Descriptive name of the remote output.
     * @param p_networkTbl Network table name.
     * @param p_default Default value.
     */
    public WsRemoteDigitalOutput(String name, String p_networkTbl, boolean p_default) {
        super(name, p_default);
        NetworkTable remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);
        publisher = remoteIOTable.getBooleanTopic(name).publish();
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