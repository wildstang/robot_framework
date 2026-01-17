package org.wildstang.hardware.roborio.outputs.config;

import org.wildstang.framework.hardware.OutputConfig;

/**
 * Contains configurations for Phoenix Talon and Victor motor controllers.
 */
public class WsTalonConfig implements OutputConfig {

    private int m_channel = 0;
    private double m_default;
    private WsMotorControllers controller;
    private boolean invert;

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     */
    public WsTalonConfig(int channel, WsMotorControllers controller) {
        this(channel, controller, false, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param invert True if motor output should be inverted.
     */
    public WsTalonConfig(int channel, WsMotorControllers controller, boolean invert) {
        this(channel, controller, invert, 0);
    }

    /**
     * Construct the Phoenix config.
     * @param channel Controller CAN constant.
     * @param controller Enumeration representing type of controller.
     * @param invert True if motor output should be inverted.
     * @param p_default Default output value.
     */
    public WsTalonConfig(int channel, WsMotorControllers controller, boolean invert, double p_default) {
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
     * Returns the default output value.
     * @return The default value.
     */
    public double getDefault() {
        return m_default;
    }

    /**
     * Returns true if the motor controller is a Talon.
     * @return True if Talon, false if Victor.
     */
    public WsMotorControllers getType() {
        return controller;
    }

    /**
     * Returns if the controller should be inverted.
     * @return True if inverted.
     */
    public boolean isInverted() {
        return invert;
    }

    /**
     * Builds a JSON String describing the Phoenix config.
     * @return Channel number and controller type.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"channel\": ");
        buf.append(m_channel);
        buf.append(", \"controller\": ");
        buf.append(controller);
        buf.append("}");
        return buf.toString();
    }

}
