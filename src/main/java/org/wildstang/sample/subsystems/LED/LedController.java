package org.wildstang.sample.subsystems.LED;

import org.wildstang.framework.core.Core;
import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.Robot;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.targeting.WsVision;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LedController implements Subsystem {

    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;
    private WsVision vision;
    private Timer timer =  new Timer();

    private int port = 0;//port
    private int length = 45;//length
    private int initialHue = 0;
    private int initialRed = 0;
    private int initialBlue = 0;

    private int[] white = {255,255,255};
    private int[] blue = {0,0,255};
    private int[] red = {255,0,0};
    private int[] green = {0,255,0};
    private int[] orange = {255,100,0};
    private int[] cyan = {0,155,155};
    private int[] purple = {128,0,128};


    @Override
    public void update(){
        if (Core.getIsDisabledMode()){
            if (Core.isAutoLocked()){
                if (Core.isBlue()) cycleBlue();
                else cycleRed();
            } else rainbow();
        } else {

            
            if (Core.isAutoLocked()){
                if (Core.isBlue()) cycleBlue();
                else cycleRed();
            } else rainbow();
        }
        led.setData(ledBuffer);
        led.start();
    }

    @Override
    public void inputUpdate(Input source) {
        
    }

    @Override
    public void initSubsystems() {      
        vision = (WsVision) Core.getSubsystemManager().getSubsystem(WsSubsystems.WS_VISION);   
    }

    @Override
    public void init() {
        
        //Outputs
        led = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);
        led.setLength(ledBuffer.getLength());
        setRGB(255, 255, 255);
        led.setData(ledBuffer);
        led.start();
        resetState();
        timer.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void resetState() {
    }

    @Override
    public String getName() {
        return "Led Controller";
    }

    public void setRGB(int red, int green, int blue){
        for (int i = 0; i < length; i++){
            ledBuffer.setRGB(i, (int)(0.75*red), (int)(0.75*green), (int)(0.75*blue));
        }
    }
    public void setRGB(int[] color){
        setRGB(color[0],color[1],color[2]);
    }

    private void rainbow(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setHSV(i, 180-(initialHue + (i*180/ledBuffer.getLength()))%180, 255, 128);
        }
        initialHue = (initialHue + 3) % 180;
        led.setData(ledBuffer);
    } 
    private void cycleRed(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setRGB(i, (initialRed + 255-(i*255/ledBuffer.getLength()))%255, 0, 0);
        }
        initialRed = (initialRed + 5) % 255;
    }
    private void cycleBlue(){
        for (int i = 0; i < ledBuffer.getLength(); i++){
            ledBuffer.setRGB(i, 0, 0, 255-(initialBlue + (i*255/ledBuffer.getLength()))%255);
        }
        initialBlue = (initialBlue + 5) % 255;
    }
}
