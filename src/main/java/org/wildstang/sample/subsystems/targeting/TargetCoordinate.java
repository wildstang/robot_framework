package org.wildstang.sample.subsystems.targeting;

import org.wildstang.framework.core.Core;

public class TargetCoordinate{
        private double blueX, blueY, redX, redY, blueHeading, redHeading;

        public TargetCoordinate(double i_blueX, double i_blueY){
            this(i_blueX, i_blueY, i_blueX, 322-i_blueY, 0, 0);
        }
        public TargetCoordinate(double i_blueX, double i_blueY, double i_redX, double i_redY, double i_blueHeading, double i_redHeading){
            blueX = i_blueX;
            blueY = i_blueY;
            redX = i_redX;
            redY = i_redY;
            blueHeading = i_blueHeading;
            redHeading = i_redHeading;
        }
        public TargetCoordinate(double i_blueX, double i_blueY, double i_redX, double i_redY){
            this(i_blueX, i_blueY, i_redX, i_redY,0,0);
        }
        public TargetCoordinate(double i_blueX, double i_blueY, double i_blueHeading){
            this(i_blueX, i_blueY, i_blueX, 322-i_blueY, i_blueHeading, 360-i_blueHeading);
        }
        public double getX(){
            return Core.isBlue() ? blueX : redX;
        }
        public double getY(){
            return Core.isBlue() ? blueY : redY;
        }
        public double getHeading(){
            return Core.isBlue() ? blueHeading : redHeading;
        }
    }
