package org.wildstang.hardware.roborio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

/**
 * Contains configurations for Spark Max motor controllers.
 */
public class WsSparkMaxConfig implements OutputConfig {

    private int m_channel = 0;
    private double m_default;
    private boolean brushless;
    private boolean invert;

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param brushless True if the motor is brushless, false if brushed.
     */
    public WsSparkMaxConfig(int channel, boolean brushless) {
        this(channel, brushless, false, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param invert True if motor output should be inverted.
     */
    public WsSparkMaxConfig(int channel, boolean brushless, boolean invert) {
        this(channel, brushless, invert, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param brushless True if the motor is brushless, false if brushed.
     * @param invert True if motor output should be inverted.
     * @param p_default Default output value.
     */
    public WsSparkMaxConfig(int channel, boolean brushless, boolean invert, double p_default) {
        m_channel = channel;
        this.brushless = brushless;
        this.invert = invert;
        m_default = p_default;
    }

    /**
     * Returns the hardware port number.
     * @return The hardware port number.
     */
    public int getChannel() {
        return m_channel;
    }

    /**
     * Returns the default output value.
     * @return The default value.
     */
    public double getDefault() {
        return m_default;
    }

    /**
     * Returns true if the motor is brushless.
     * @return True if the motor is brushless, false if brushed.
     */
    public boolean isBrushless() {
        return brushless;
    }

    /**
     * Returns if the controller should be inverted.
     * @return True if inverted.
     */
    public boolean isInverted() {
        return invert;
    }

    /**
     * Builds a JSON String describing the Spark Max config.
     * @return Channel number and is talon.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append(", \"brushless\": ");
        buf.append(brushless);
        buf.append("}");
        return buf.toString();
    }

}
