package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.sample.robot.WsInputs;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WsVision implements Subsystem {

    public WsLL left = new WsLL("limelight-left");
    public WsLL right = new WsLL("limelight-right");

    public boolean gamepiece, isLow;

    private DigitalInput rightBumper, leftBumper, high, mid, low;//, dup, ddown;

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public boolean TargetInView(){
        return left.TargetInView() || right.TargetInView();
    }

    //get ySpeed value for auto drive
    public double getScoreY(double offset){
        if (right.TargetInView() && left.TargetInView()){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (getLeftVertical() + getRightVertical())/2.0);
        } else if (left.tv > 0.0){
            return (getLeftVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        } else {
            return (getRightVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        }
    }
    //get ySpeed value for station auto drive
    public double getStationY(double offset){
        if (left.TargetInView()){
            return (offset*LC.STATION_OFFSETS + (left.tid > 4.5 ? -left.blue3D[0] + LC.STATION_VERTICAL 
                : -left.red3D[0] + LC.STATION_VERTICAL)) * -LC.VERT_AUTOAIM_P;
        } else {
            return (offset*LC.STATION_OFFSETS + (right.tid > 4.5 ? -right.blue3D[0] + LC.STATION_VERTICAL 
                : -right.red3D[0] + LC.STATION_VERTICAL)) * -LC.VERT_AUTOAIM_P;
        }
    }
    //get xSpeed value for station auto drive
    public double getStationX(double offset){
        if (left.TargetInView()){
            if (left.isSeeingBlue()){
                //basically this garbage line determines whether we're going to the left or right double substation
                //  by which one's closer, adds on the driver offset, and then calculates the xSpeed for that
                //  desired displacement.
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (left.blue3D[1] - LC.BLUE_STATION_X - 
                    LC.STATION_HORIZONTAL*Math.signum(left.blue3D[1]-LC.BLUE_STATION_X)));
            } else {
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (left.red3D[1] - LC.RED_STATION_X - 
                LC.STATION_HORIZONTAL*Math.signum(left.red3D[1]-LC.RED_STATION_X)));
            }
        } else {
            if (right.isSeeingBlue()){
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (right.blue3D[1] - LC.BLUE_STATION_X - 
                LC.STATION_HORIZONTAL*Math.signum(right.blue3D[1]-LC.BLUE_STATION_X)));
            } else {
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (right.red3D[1] - LC.RED_STATION_X - 
                LC.STATION_HORIZONTAL*Math.signum(right.red3D[1]-LC.RED_STATION_X)));
            }
        }
    }
    //gets the vertical distance to target for grid targets from the left limelight
    private double getLeftVertical(){
        if (left.isSeeingBlue()){
            return -left.blue3D[0] + (LC.VERTICAL_APRILTAG_DISTANCE + (isLow ? 10.0 : 0.0)); 
        } else {
            return -left.red3D[0] + (LC.VERTICAL_APRILTAG_DISTANCE + (isLow ? 10.0 : 0.0));
        }
    }
    //gets the vertical distance to target for grid targets from the right limelight
    private double getRightVertical(){
        if (right.isSeeingBlue()){
            return -right.blue3D[0] + (LC.VERTICAL_APRILTAG_DISTANCE); 
        } else {
            return -right.red3D[0] + (LC.VERTICAL_APRILTAG_DISTANCE);
        }
    }

    //get xSpeed value for autodrive
    public double getScoreX(double offset){
        if (right.TargetInView() && left.TargetInView()){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + (getLeftHorizontal() + getRightHorizontal())/2.0);
        } else if (left.TargetInView()){
            return (getLeftHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        } else {
            return (getRightHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        }
    }
    //get the horizontal distance to targets on the grid from the left limelight
    private double getLeftHorizontal(){
        if (left.isSeeingBlue()){
            if (gamepiece) return getCone(left.blue3D[1], true);
            else return getCube(left.blue3D[1], true);
        } else {
            if (gamepiece) return getCone(left.red3D[1], false);
            else return getCube(left.red3D[1], false);
        }
    }
    //gets the horizontal distance to targets on the grid from the right limelight
    private double getRightHorizontal(){
        if (right.isSeeingBlue()){
            if (gamepiece) return getCone(right.blue3D[1], true);
            else return getCube(right.blue3D[1], true);
        } else {
            if (gamepiece) return getCone(right.red3D[1], false);
            else return getCube(right.red3D[1], false);
        }
    }
    //determines which cone node is the closest to score on
    private double getCone(double target, boolean color){
        int i = color ? 6 : 0;
        double minimum = 1000;
        while (i < (color ? 12 : 6)){
            if (Math.abs(minimum) > Math.abs(target - LC.CONES[i]*39.3701)){
                minimum = target - LC.CONES[i]*39.3701;
            }
            i++;
        }
        return minimum;
    }
    //determines which cube node is the closest to score on
    private double getCube(double target, boolean color){
        int i = color ? 3 : 0;
        double minimum = 1000;
        while (i < (color ? 6 : 3)){
            if (Math.abs(minimum) > Math.abs(target - LC.CUBES[i]*39.3701)){
                minimum = target - LC.CUBES[i]*39.3701;
            }
            i++;
        }
        return minimum;
    }

    @Override
    public void inputUpdate(Input source) {
        if (rightBumper.getValue())
        {
            gamepiece = LC.CUBE;
        }
        if (leftBumper.getValue()){
            gamepiece = LC.CONE;
        }
        if (source == low && low.getValue()) isLow = true;
        if ((source == mid && mid.getValue()) || (source == high && high.getValue())) isLow = false;
    }

    @Override
    public void init() {
        LC = new LimeConsts();

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WsInputs.OPERATOR_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WsInputs.OPERATOR_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        high = (DigitalInput) WsInputs.OPERATOR_FACE_UP.get();
        high.addInputListener(this);
        mid = (DigitalInput) WsInputs.OPERATOR_FACE_RIGHT.get();
        mid.addInputListener(this);
        low = (DigitalInput) WsInputs.OPERATOR_FACE_DOWN.get();
        low.addInputListener(this);

        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        left.update();
        right.update();
        SmartDashboard.putBoolean("limelight target in view", TargetInView());
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
        isLow = false;
        left.update();
        right.update();
    }
    public void setGamePiece(boolean newPiece){
        gamepiece = newPiece;
    }

    @Override
    public String getName() {
        return "Ws Vision";
    }
}