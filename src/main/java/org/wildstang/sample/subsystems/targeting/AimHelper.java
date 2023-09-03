package org.wildstang.sample.subsystems.targeting;

// ton of imports
import org.wildstang.framework.subsystems.Subsystem;
import org.wildstang.hardware.roborio.inputs.WsRemoteAnalogInput;
import org.wildstang.framework.core.Core;

import org.wildstang.framework.io.inputs.DigitalInput;
import org.wildstang.framework.io.inputs.Input;
import org.wildstang.sample.robot.WSInputs;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AimHelper implements Subsystem {
    
    private static final double mToIn = 39.3701;

    public NetworkTable limeleft = NetworkTableInstance.getDefault().getTable("limelight-left");
    public NetworkTable limeright = NetworkTableInstance.getDefault().getTable("limelight-right");

    public LimelightHelpers.Results lresult;
    public LimelightHelpers.Results rresult;

    public double[] ltarget3D;
    public double[] rtarget3D;
    public double[] lblue3D;
    public double[] lred3D;
    public double[] rblue3D;
    public double[] rred3D;
    public double ltid;
    public double rtid;
    public double ltv;
    public double rtv;

    public int lnumtargets;
    public int rnumtargets;

    public boolean gamepiece, isLow;

    private DigitalInput rightBumper, leftBumper, high, mid, low;//, dup, ddown;

    public LimeConsts LC;

    ShuffleboardTab tab = Shuffleboard.getTab("Tab");

    public void calcTargetCoords() { //update target coords.
        ltv = limeleft.getEntry("tv").getDouble(0);
        rtv = limeright.getEntry("tv").getDouble(0);
        if(ltv > 0.0) {
            ltarget3D = limeleft.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            lblue3D = limeleft.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
            lred3D = limeleft.getEntry("botpose_wpired").getDoubleArray(new double[7]);
            ltid = limeleft.getEntry("tid").getDouble(0);
            lnumtargets = lresult.targets_Fiducials.length;
        }
        if (rtv > 0.0){
            rtarget3D = limeright.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
            rblue3D = limeright.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
            rred3D = limeright.getEntry("botpose_wpired").getDoubleArray(new double[7]);
            rtid = limeright.getEntry("tid").getDouble(0);
            rnumtargets = rresult.targets_Fiducials.length;
        }
    }

    public boolean TargetInView(){
        return ltv > 0.0 || rtv > 0.0;
    }

    //get ySpeed value for auto drive
    public double getScoreY(double offset){
        if (rtv > 0.0 && ltv > 0.0){
            return LC.VERT_AUTOAIM_P * (offset*LC.OFFSET_VERTICAL + (getLeftVertical() + getRightVertical())/2.0);
        } else if (ltv > 0.0){
            return (getLeftVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        } else {
            return (getRightVertical()+offset*LC.OFFSET_VERTICAL) * LC.VERT_AUTOAIM_P;
        }
    }
    //get ySpeed value for station auto drive
    public double getStationY(double offset){
        if (ltv > 0.0){
            return (offset*LC.STATION_OFFSETS + (ltid > 4.5 ? -lblue3D[0]*mToIn + LC.STATION_VERTICAL 
                : -lred3D[0]*mToIn + LC.STATION_VERTICAL)) * -LC.VERT_AUTOAIM_P;
        } else {
            return (offset*LC.STATION_OFFSETS + (rtid > 4.5 ? -rblue3D[0]*mToIn + LC.STATION_VERTICAL 
                : -rred3D[0]*mToIn + LC.STATION_VERTICAL)) * -LC.VERT_AUTOAIM_P;
        }
    }
    //get xSpeed value for station auto drive
    public double getStationX(double offset){
        if (ltv > 0.0){
            if (ltid > 4.5){
                //basically this garbage line determines whether we're going to the left or right double substation
                //  by which one's closer, adds on the driver offset, and then calculates the xSpeed for that
                //  desired displacement.
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (lblue3D[1]*mToIn - LC.BLUE_STATION_X*mToIn - 
                    LC.STATION_HORIZONTAL*Math.signum(lblue3D[1]-LC.BLUE_STATION_X)));
            } else {
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (lred3D[1]*mToIn - LC.RED_STATION_X*mToIn - 
                LC.STATION_HORIZONTAL*Math.signum(lred3D[1]-LC.RED_STATION_X)));
            }
        } else {
            if (rtid > 4.5){
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (rblue3D[1]*mToIn - LC.BLUE_STATION_X*mToIn - 
                LC.STATION_HORIZONTAL*Math.signum(rblue3D[1]-LC.BLUE_STATION_X)));
            } else {
                return -LC.HORI_AUTOAIM_P * (-offset*LC.STATION_OFFSETS + (rred3D[1]*mToIn - LC.RED_STATION_X*mToIn - 
                LC.STATION_HORIZONTAL*Math.signum(rred3D[1]-LC.RED_STATION_X)));
            }
        }
    }
    //gets the vertical distance to target for grid targets from the left limelight
    private double getLeftVertical(){
        if (ltid > 4.5){
            return -lblue3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE + (isLow ? 10.0 : 0.0)); 
        } else {
            return -lred3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE + (isLow ? 10.0 : 0.0));
        }
    }
    //gets the vertical distance to target for grid targets from the right limelight
    private double getRightVertical(){
        if (rtid > 4.5){
            return -rblue3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE); 
        } else {
            return -rred3D[0]*mToIn + (LC.VERTICAL_APRILTAG_DISTANCE);
        }
    }

    //get xSpeed value for autodrive
    public double getScoreX(double offset){
        if (rtv > 0.0 && ltv > 0.0){
            return LC.HORI_AUTOAIM_P * (offset*LC.OFFSET_HORIZONTAL + (getLeftHorizontal() + getRightHorizontal())/2.0);
        } else if (ltv > 0.0){
            return (getLeftHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        } else {
            return (getRightHorizontal()+offset*LC.OFFSET_HORIZONTAL) * LC.HORI_AUTOAIM_P;
        }
    }
    //get the horizontal distance to targets on the grid from the left limelight
    private double getLeftHorizontal(){
        if (ltid > 4.5){
            if (gamepiece) return getCone(lblue3D[1]*mToIn, true);
            else return getCube(lblue3D[1]*mToIn, true);
        } else {
            if (gamepiece) return getCone(lred3D[1]*mToIn, false);
            else return getCube(lred3D[1]*mToIn, false);
        }
    }
    //gets the horizontal distance to targets on the grid from the right limelight
    private double getRightHorizontal(){
        if (rtid > 4.5){
            if (gamepiece) return getCone(rblue3D[1]*mToIn, true);
            else return getCube(rblue3D[1]*mToIn, true);
        } else {
            if (gamepiece) return getCone(rred3D[1]*mToIn, false);
            else return getCube(rred3D[1]*mToIn, false);
        }
    }
    //determines which cone node is the closest to score on
    private double getCone(double target, boolean color){
        int i = color ? 6 : 0;
        double minimum = 1000;
        while (i < (color ? 12 : 6)){
            if (Math.abs(minimum) > Math.abs(target - LC.CONES[i]*mToIn)){
                minimum = target - LC.CONES[i]*mToIn;
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
            if (Math.abs(minimum) > Math.abs(target - LC.CUBES[i]*mToIn)){
                minimum = target - LC.CUBES[i]*mToIn;
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

        ltv = limeleft.getEntry("tv").getDouble(0);
        rtv = limeright.getEntry("tv").getDouble(0);
        ltarget3D = limeleft.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        rtarget3D = limeright.getEntry("botpose_targetspace").getDoubleArray(new double[6]);
        lblue3D = limeleft.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        lred3D = limeleft.getEntry("botpose_wpired").getDoubleArray(new double[7]);
        rblue3D = limeright.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        rred3D = limeright.getEntry("botpose_wpired").getDoubleArray(new double[7]);
        ltid = limeleft.getEntry("tid").getDouble(0);
        rtid = limeright.getEntry("tid").getDouble(0);

        rightBumper = (DigitalInput) Core.getInputManager().getInput(WSInputs.MANIPULATOR_RIGHT_SHOULDER);
        rightBumper.addInputListener(this);
        leftBumper = (DigitalInput) WSInputs.MANIPULATOR_LEFT_SHOULDER.get();
        leftBumper.addInputListener(this);
        high = (DigitalInput) WSInputs.MANIPULATOR_FACE_UP.get();
        high.addInputListener(this);
        mid = (DigitalInput) WSInputs.MANIPULATOR_FACE_RIGHT.get();
        mid.addInputListener(this);
        low = (DigitalInput) WSInputs.MANIPULATOR_FACE_DOWN.get();
        low.addInputListener(this);

        resetState();
    }

    @Override
    public void selfTest() {
    }

    @Override
    public void update() {
        lresult = LimelightHelpers.getLatestResults("limelight-left").targetingResults;
        rresult = LimelightHelpers.getLatestResults("limelight-right").targetingResults;
        calcTargetCoords();
        SmartDashboard.putBoolean("limeleft tiv", ltv > 0.0);
        SmartDashboard.putNumber("limeleft tid", ltid);
        SmartDashboard.putNumber("limeleft red X", lred3D[0]*mToIn);
        SmartDashboard.putNumber("limeleft red Y", lred3D[1]*mToIn);
        SmartDashboard.putNumber("limeleft blue X", lblue3D[0]*mToIn);
        SmartDashboard.putNumber("limeleft blue Y", lblue3D[1]*mToIn);
        SmartDashboard.putBoolean("limeright tiv", rtv > 0.0);
        SmartDashboard.putNumber("limeright tid", rtid);
        SmartDashboard.putNumber("limeright red X", rred3D[0]*mToIn);
        SmartDashboard.putNumber("limeright red Y", rred3D[1]*mToIn);
        SmartDashboard.putNumber("limeright blue X", rblue3D[0]*mToIn);
        SmartDashboard.putNumber("limeright blue Y", rblue3D[1]*mToIn);
        SmartDashboard.putBoolean("limelight target in view", TargetInView());
    }

    @Override
    public void resetState() {
        gamepiece = LC.CONE;
        isLow = false;
        ltid = 1;
        rtid = 1;
        ltv = 0;
        rtv = 0;
        lnumtargets = 0;
        rnumtargets = 0;
    }
    public void setGamePiece(boolean newPiece){
        gamepiece = newPiece;
    }

    @Override
    public String getName() {
        return "Aim Helper";
    }
}