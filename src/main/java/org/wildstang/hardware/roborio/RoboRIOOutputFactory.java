package org.wildstang.hardware.roborio;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;
import org.wildstang.framework.hardware.OutputFactory;
import org.wildstang.framework.io.outputs.Output;
import org.wildstang.hardware.roborio.outputs.WsDigitalOutput;
import org.wildstang.hardware.roborio.outputs.WsDoubleSolenoid;
import org.wildstang.hardware.roborio.outputs.WsI2COutput;
// import org.wildstang.hardware.roborio.outputs.WsPhoenix;
import org.wildstang.hardware.roborio.outputs.WsRelay;
import org.wildstang.hardware.roborio.outputs.WsServo;
import org.wildstang.hardware.roborio.outputs.WsSolenoid;
import org.wildstang.hardware.roborio.outputs.WsSpark;
import org.wildstang.hardware.roborio.outputs.WsRemoteAnalogOutput;
import org.wildstang.hardware.roborio.outputs.WsRemoteDigitalOutput;
import org.wildstang.hardware.roborio.outputs.config.WsDigitalOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsDoubleSolenoidConfig;
import org.wildstang.hardware.roborio.outputs.config.WsI2COutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsTalonConfig;
import org.wildstang.hardware.roborio.outputs.config.WsTalonFollowerConfig;
import org.wildstang.hardware.roborio.outputs.config.WsRelayConfig;
import org.wildstang.hardware.roborio.outputs.config.WsServoConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSolenoidConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkConfig;
import org.wildstang.hardware.roborio.outputs.config.WsSparkFollowerConfig;
import org.wildstang.hardware.roborio.outputs.config.WsRemoteAnalogOutputConfig;
import org.wildstang.hardware.roborio.outputs.config.WsRemoteDigitalOutputConfig;

/**
 * Builds outputs from WsOutputs enumerations.
 */
public class RoboRIOOutputFactory implements OutputFactory {

    private boolean s_initialised = false;

    /**
     * Empty constructor override WPILib InputFactory constructor.
     */
    public RoboRIOOutputFactory() {}

    /**
     * Prepares logger.
     */
    public void init() {
        if (!s_initialised) {
            s_initialised = true;
        }
    }

    /**
     * Creates an Output from an enumeration of WsOutputs.
     * @param p_output An enumeration of WsOutputs.
     * @return A constructed Output.
     */
    public Output createOutput(Outputs p_output) {
        Output out = null;
        OutputConfig config = p_output.getConfig();

        if (config instanceof WsServoConfig) {
            WsServoConfig c = (WsServoConfig) config;
            out = new WsServo(p_output.getName(), c.getChannel(), c.getDefault());
        }
        else if (config instanceof WsRelayConfig) {
            WsRelayConfig c = (WsRelayConfig) config;
            out = new WsRelay(p_output.getName(), c.getChannel());
        }
        // else if (config instanceof WsPhoenixConfig) {
        //     WsPhoenixConfig c = (WsPhoenixConfig) config;
        //     out = new WsPhoenix(p_output.getName(), c.getChannel(), c.getDefault(),
        //                         c.getType(), c.isInverted());
        // }
        // Note a WsPhoenixFollower must be defined after its corresponding WsPhoenix
        // else if (config instanceof WsPhoenixFollowerConfig) {
        //     WsPhoenixFollowerConfig c = (WsPhoenixFollowerConfig) config;
        //     // Returns the follwed WsPhoenix because a return is required
        //     // and duplicate outputs are thrown out when encountered.
        //     out = Core.getOutputManager().getOutput(c.getFollowing());
        //     ((WsPhoenix) out).addFollower(c.getChannel(), c.getType(), c.isOpposing());
        // }
        else if (config instanceof WsSparkConfig) {
            WsSparkConfig c = (WsSparkConfig) config;
            out = new WsSpark(p_output.getName(), c.getChannel(), c.getType(), c.getDefault(), c.isInverted());
        }
        // Note a WsSparkMaxFollower must be defined after its corresponding WsSparkMax
        else if (config instanceof WsSparkFollowerConfig) {
            WsSparkFollowerConfig c = (WsSparkFollowerConfig) config;
            // Returns the follwed WsSparkMax because a return is required
            // and duplicate outputs are thrown out when encountered.
            out = Core.getOutputManager().getOutput(c.getFollowing());
            ((WsSpark) out).addFollower(c.getChannel(), c.getType(), c.isOpposing());
        }
        else if (config instanceof WsSolenoidConfig) {
            WsSolenoidConfig c = (WsSolenoidConfig) config;
            out = new WsSolenoid(p_output.getName(), c.getModule(), c.getChannel(), c.getDefault());
        }
        else if (config instanceof WsDoubleSolenoidConfig) {
            WsDoubleSolenoidConfig c = (WsDoubleSolenoidConfig) config;
            out = new WsDoubleSolenoid(p_output.getName(), c.getModule(), c.getChannel1(),
                                        c.getChannel2(), c.getDefault());
        }
        else if (config instanceof WsI2COutputConfig) {
            WsI2COutputConfig c = (WsI2COutputConfig) config;
            out = new WsI2COutput(p_output.getName(), c.getPort(), c.getAddress());
        }
        else if (config instanceof WsRemoteDigitalOutputConfig) {
            WsRemoteDigitalOutputConfig c = (WsRemoteDigitalOutputConfig) config;
            out = new WsRemoteDigitalOutput(p_output.getName(), c.getTableName(),c.getDefault());
        }
        else if (config instanceof WsRemoteAnalogOutputConfig) {
            WsRemoteAnalogOutputConfig c = (WsRemoteAnalogOutputConfig) config;
            out = new WsRemoteAnalogOutput(p_output.getName(), c.getTableName(), c.getDefault());
        }
        else if (config instanceof WsDigitalOutputConfig) {
            WsDigitalOutputConfig c = (WsDigitalOutputConfig) config;
            out = new WsDigitalOutput(p_output.getName(), c.getChannel(), c.getDefault());
        }

        return out;
    }

}
