package org.wildstang.hardware.roborio.outputs.config;

import org.wildstang.framework.core.Outputs;
import org.wildstang.framework.hardware.OutputConfig;

/**
 * Contains configurations for Spark Max/Flex motor controller followers.
 */
public class WsSparkFollowerConfig implements OutputConfig {

    private Outputs following;
    private int m_channel = 0;
    private boolean oppose;
    private WsMotorControllers controller;

    /**
     * Construct the Phoenix config.
     * @param following Name of motor controller being followed.
     * @param channel Hardware port number.
     * @param controller Enumeration representing type of controller.
     */
    public WsSparkFollowerConfig(Outputs following, int channel, WsMotorControllers controller) {
        this(following, channel, controller, false);
    }

    /**
     * Construct the Phoenix config.
     * @param following Name of motor controller being followed.
     * @param channel Hardware port number.
     * @param controller Enumeration representing type of controller.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public WsSparkFollowerConfig(Outputs following, int channel, WsMotorControllers controller, boolean oppose) {
        this.following = following;
        m_channel = channel;
        this.controller = controller;
        this.oppose = oppose;
    }

    /**
     * Returns the name of the followed motor controller.
     * @return The name of followed motor controller.
     */
    public Outputs getFollowing() {
        return following;
    }

    /**
     * Returns the hardware port number.
     * @return The hardware port number.
     */
    public int getChannel() {
        return m_channel;
    }

    /**
     * Returns true if the motor controller is a Talon.
     * @return True if Talon, false if Victor.
     */
    public WsMotorControllers getType() {
        return controller;
    }

    /**
     * Returns if the follower should oppose the followed.
     * @return True if opposing.
     */
    public boolean isOpposing() {
        return oppose;
    }

    /**
     * Builds a JSON String describing the Phoenix config.
     * @return Channel number, is brushless, and is opposing.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append(", \"type\": ");
        buf.append(controller.name());
        buf.append(", \"oppose\": ");
        buf.append(oppose);
        buf.append("}");
        return buf.toString();
    }

}
