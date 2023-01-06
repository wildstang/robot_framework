package org.wildstang.hardware.roborio.inputs;

import org.wildstang.framework.io.inputs.AnalogInput;

import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Reads a remote analog input.
 */
public class WsRemoteAnalogInput extends AnalogInput {

    DoubleSubscriber subscriber;

    /**
     * Construct the remote input.
     * @param p_name Name of the input.
     * @param p_networkTbl Network table name.
     */
    public WsRemoteAnalogInput(String p_name, String p_networkTbl) {
        super(p_name);
        NetworkTable remoteIOTable = NetworkTableInstance.getDefault().getTable(p_networkTbl);
        subscriber = remoteIOTable.getDoubleTopic(p_name).subscribe(0);
    }

    /**
     * Reads the value from the remote analog input.
     * @return Raw value from input.
     */
    @Override
    public double readRawValue() {
        return subscriber.get();
    }

}