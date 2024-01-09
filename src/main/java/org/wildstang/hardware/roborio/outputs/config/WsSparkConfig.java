package org.wildstang.hardware.roborio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

/**
 * Contains configurations for Spark Max/Flex motor controllers.
 */
public class WsSparkConfig implements OutputConfig {

    private int m_channel = 0;
    private double m_default;
    private WsMotorControllers controller;
    private boolean invert;

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     */
    public WsSparkConfig(int channel, WsMotorControllers controller) {
        this(channel, controller, false, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param invert True if motor output should be inverted.
     */
    public WsSparkConfig(int channel, WsMotorControllers controller, boolean invert) {
        this(channel, controller, invert, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param invert True if motor output should be inverted.
     * @param p_default Default output value.
     */
    public WsSparkConfig(int channel, WsMotorControllers controller, boolean invert, double p_default) {
        m_channel = channel;
        this.controller = controller;
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
     * Returns the motor controller type.
     * @return The hardware motor controller type.
     */
    public WsMotorControllers getType() {
        return controller;
    }

    /**
     * Returns the default output value.
     * @return The default value.
     */
    public double getDefault() {
        return m_default;
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
        buf.append(", \"type\": ");
        buf.append(controller.name());
        buf.append("}");
        return buf.toString();
    }

}
