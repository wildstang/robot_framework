package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.sample.robot.WsInputs;
import org.wildstang.sample.robot.WsSubsystems;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsVision implements Subsystem {

    public WsLL left = new WsLL("limelight-left");
    public WsLL right = new WsLL("limelight-right");
    public WsLL back = new WsLL("limelight-back");

    private final double Align_P = 0.006;
    
    public SwerveDrive swerve;

    public VisionConsts VC;
    public Translation2d robotOdometry = new Translation2d();

    private double inputDistance = 0;

    private DigitalInput driverLeftShoulder;

    private Timer lastUpdate = new Timer();

    @Override
    public void inputUpdate(Input source) {

    }

    @Override
    public void initSubsystems() {
        swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem(WsSubsystems.SWERVE_DRIVE);
    }

    @Override
    public void init() {
        VC = new VisionConsts();
        //same as update()
        //resetState();
        driverLeftShoulder = (DigitalInput) WsInputs.DRIVER_LEFT_SHOULDER.get();
        driverLeftShoulder.addInputListener(this);
        lastUpdate.start();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {

        left.update(swerve.getFieldYaw());
        right.update(swerve.getFieldYaw());
        back.update(swerve.getFieldYaw());
        if (aprilTagsInView()) lastUpdate.reset();
        SmartDashboard.putBoolean("Vision targetinView", aprilTagsInView());
        SmartDashboard.putNumber("GP X", back.tx);
        SmartDashboard.putNumber("GP Y", back.ty);
        SmartDashboard.putBoolean("GP tv", back.TargetInView());
        SmartDashboard.putNumber("Vision back tx", back.tx);
        SmartDashboard.putNumber("Vision back ty", back.ty);

    }

    @Override
    public void resetState() {
        left.update(swerve.getFieldYaw());
        right.update(swerve.getFieldYaw());
        back.update(swerve.getFieldYaw());
    }

    @Override
    public String getName() {
        return "Ws Vision";
    }

    /**
     *  determine whether to take data from left or right camera
     */
    public boolean isLeftBetter(){
        if (left.TargetInView() && !right.TargetInView()) return true;
        if (!left.TargetInView() && right.TargetInView()) return false;
        if (left.getTagDist() < right.getTagDist()) return true;
        if (left.getTagDist() > right.getTagDist()) return false;
        if (left.getNumTags() > right.getNumTags()) return true;
        if (left.getNumTags() < right.getNumTags()) return false;
        return false;
    }
    /**
     *  gets bearing degrees for what the robot's heading should be to be pointing at the target
     */
    public double turnToTarget(TargetCoordinate target){
        if (!aprilTagsInView()) return left.getDirection(robotOdometry.getX() - target.getX(), robotOdometry.getY() - target.getY());
        return isLeftBetter() ? left.turnToTarget(target) : right.turnToTarget(target);
    }
    /*
     * returns distance from current location to target, in inches
     */
    public double distanceToTarget(TargetCoordinate target){
        if (!aprilTagsInView()) return Math.hypot(robotOdometry.getX() - target.getX(), robotOdometry.getY() - target.getY());
        else return isLeftBetter() ? left.distanceToTarget(target) : right.distanceToTarget(target);
    }
    /**
     * can either camera see an April Tag
     */ 
    public boolean aprilTagsInView(){
        return left.TargetInView() || right.TargetInView();
    }
    /*
     * get the control value to use for driving the robot to a specific X on the field
     */
    public double getXAdjust(TargetCoordinate target){
        if (!aprilTagsInView()) return Align_P * robotOdometry.getX();
        if (isLeftBetter()) return Align_P * left.getAlignX(target);
        else return Align_P * right.getAlignX(target);
    }
    /*
     * get the control value to use for driving the robot to a specific y on the field
     */
    public double getYAdjust(TargetCoordinate target){
        if (!aprilTagsInView()) return Align_P * robotOdometry.getY();
        if (isLeftBetter()) return Align_P * left.getAlignY(target);
        else return Align_P * right.getAlignY(target);
    }
    /*
     * get Y value from cameras to use for determining the direction of the robot
     * to feed a note to the desired location
     */
    public double getYValue(){
        if (!aprilTagsInView()) return robotOdometry.getY();
        if (isLeftBetter()) return left.target3D[1];
        else return right.target3D[1];
    }
    public double getUpdateTime(){
        return lastUpdate.get();
    }
    public Translation2d getCameraPose(){
        if (isLeftBetter()) return new Translation2d(left.target3D[0], left.target3D[1]);
        else return new Translation2d(right.target3D[0], right.target3D[1]);
    }
    public void setOdometry(Translation2d update){
        robotOdometry = update;
        robotOdometry.times(39.37);
    }
    }