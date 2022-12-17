package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;

import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.AnalogInput;
import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.sample.robot.WSInputs;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wildstang.sample.subsystems.swerve.DriveConstants;
import org.wildstang.sample.subsystems.swerve.WSSwerveHelper;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class AimHelper implements Subsystem {
    
    private NetworkTable LimeTable;
    private NetworkTableEntry ty; //y angle
    private NetworkTableEntry tx; //x angle
    private NetworkTableEntry tv;
    private NetworkTableEntry ledModeEntry;
    private NetworkTableEntry llModeEntry;

    //private SwerveDrive swerve;
    private WSSwerveHelper helper;
    
    public double x;
    public double y;

    private double modifier;

    public boolean TargetInView;

    private double TargetDistance;
    private double xSpeed, ySpeed;

    private double perpFactor, parFactor;

    private DigitalInput rightBumper, dup, ddown;
    private AnalogInput leftStickX, leftStickY;

    private LimeConsts LC;

    private double gyroValue;

    private double distanceFactor = 30;
    private double angleFactor = 15;
    public static double FenderDistance = 60;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");


    public void calcTargetCoords() { //update target coords. 
        if(tv.getDouble(0) == 1) {
            x = tx.getDouble(0); 
            y = ty.getDouble(0);
            TargetInView = true;
        }
        else {
            x = 0; //no target case
            y = 0;
            TargetInView = false;
        }
        getMovingCoords();
    }

    public void getMovingCoords() {
        double robotAngle = (getGyroAngle() + 180 + tx.getDouble(0)) % 360;
        double movementAngle = helper.getDirection(xSpeed, ySpeed);
        double movementMagnitude = helper.getMagnitude(xSpeed, ySpeed);
        if (Math.abs(xSpeed) < 0.1 && Math.abs(ySpeed) < 0.1) {
            parFactor = 0;
            perpFactor = 0;
        }
        else {
            perpFactor = distanceFactor * movementMagnitude * Math.cos(Math.toRadians(-robotAngle + movementAngle));
            parFactor = angleFactor * movementMagnitude * Math.sin(Math.toRadians(-robotAngle + movementAngle));
        }
        if (!TargetInView){
            parFactor *= -0.2;
        }
    }

    private double getGyroAngle() {
        return gyroValue;
    }

    public void setGyroValue(double toSet) {
        gyroValue = toSet;
    }

    public double getDistance() {
        calcTargetCoords();
        TargetDistance = (modifier *  12) + 36 + LC.TARGET_HEIGHT / Math.tan(Math.toRadians(ty.getDouble(0) + LC.CAMERA_ANGLE_OFFSET));
        //return TargetDistance;
        return TargetDistance - perpFactor + 0.5 * Math.abs(parFactor);
    }

    public double getRotPID() {
        calcTargetCoords();
        //return tx.getDouble(0) * -0.015;
        return (tx.getDouble(0) - parFactor) * -0.015;
    }

    public void turnOnLED(boolean onState) {
        if (onState) {
            ledModeEntry.setNumber(0);//turn led on
            llModeEntry.setNumber(0);//turn camera to vision tracking
        }
        else {
            ledModeEntry.setNumber(0);//turn led off
            llModeEntry.setNumber(0);//turn camera to normal color mode
            //set above to 0 for debug mode, or for perma limelight mode
        }
    }

    @Override
    public void inputUpdate(Input source) {
        turnOnLED(rightBumper.getValue());
        if (source == dup && dup.getValue()) {
            modifier++;
        }
        if (source == ddown && ddown.getValue()) {
            modifier--;
        }
        xSpeed = leftStickX.getValue();
        ySpeed = leftStickY.getValue();
        if (Math.abs(leftStickX.getValue()) < DriveConstants.DEADBAND) {
            xSpeed = 0;
        }
        if (Math.abs(leftStickY.getValue()) < DriveConstants.DEADBAND) {
            ySpeed = 0;
        }
    }

    @Override
    public void init() {
        LC = new LimeConsts();
        x = 0;  //x and y angular offsets from limelight. Only updated when calcTargetCoords is called.
        y = 0;
        TargetInView = false; //is the target in view? only updated when calcTargetCoords is called.
        TargetDistance = 0; //distance to target in feet. Only updated when calcTargetCoords is called.


        LimeTable  = NetworkTableInstance.getDefault().getTable("limelight");

        ty = LimeTable.getEntry("ty");
        tx = LimeTable.getEntry("tx");
        tv = LimeTable.getEntry("tv");
        ledModeEntry = LimeTable.getEntry("ledMode");
        llModeEntry = LimeTable.getEntry("camMode");

        helper = new WSSwerveHelper();

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.DRIVER_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftStickX = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_X);
        leftStickX.addInputListener(this);
        leftStickY = (AnalogInput) Core.getInputManager().getInput(WSInputs.DRIVER_LEFT_JOYSTICK_Y);
        leftStickY.addInputListener(this);
        dup = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_UP);
        dup.addInputListener(this);
        ddown = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_DPAD_DOWN);
        ddown.addInputListener(this);
        resetState();
    }

    @Override
    public void selfTest() {        
    }

    @Override
    public void update() {
        calcTargetCoords();
        //distanceFactor = distance.getEntry().getDouble(0);
        //angleFactor = angle.getEntry().getDouble(0);
        SmartDashboard.putNumber("limelight distance", getDistance());    
        SmartDashboard.putNumber("limelight tx", tx.getDouble(0));
        SmartDashboard.putNumber("limelight ty", ty.getDouble(0));  
        SmartDashboard.putBoolean("limelight target in view", tv.getDouble(0)==1);  
        SmartDashboard.putNumber("Distance Modifier", modifier);
        SmartDashboard.putNumber("SWM parFactor", parFactor);
        SmartDashboard.putNumber("SWM perpFactor", perpFactor);
    }

    @Override
    public void resetState() {
        turnOnLED(false);
        modifier = 0;
        xSpeed = 0;
        ySpeed = 0;
        perpFactor = 0;
        parFactor = 0;
        gyroValue = 0;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }  
}