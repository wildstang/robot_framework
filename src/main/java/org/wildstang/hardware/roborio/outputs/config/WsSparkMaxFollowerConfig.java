package org.wildstang.hardware.roborio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

/**
 * Contains configurations for Spark Max motor controller followers.
 */
public class WsSparkMaxFollowerConfig implements OutputConfig {

    private String following;
    private int m_channel = 0;
    private boolean brushless;
    private boolean oppose;

    /**
     * Construct the Phoenix config.
     * @param following Name of motor controller being followed.
     * @param channel Hardware port number.
     * @param brushless True if the motor is brushless, false if brushed.
     */
    public WsSparkMaxFollowerConfig(String following, int channel, boolean brushless) {
        this(following, channel, brushless, false);
    }

    /**
     * Construct the Phoenix config.
     * @param following Name of motor controller being followed.
     * @param channel Hardware port number.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param oppose True if the follow should oppose the direction of this motor.
     */
    public WsSparkMaxFollowerConfig(String following, int channel, boolean brushless, boolean oppose) {
        this.following = following;
        m_channel = channel;
        this.brushless = brushless;
        this.oppose = oppose;
    }

    /**
     * Returns the name of the followed motor controller.
     * @return The name of followed motor controller.
     */
    public String getFollowing() {
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
     * Returns true if the motor is brushless.
     * @return True if the motor is brushless, false if brushed.
     */
    public boolean isBrushless() {
        return brushless;
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
        buf.append(", \"brushless\": ");
        buf.append(brushless);
        buf.append(", \"oppose\": ");
        buf.append(oppose);
        buf.append("}");
        return buf.toString();
    }

}
