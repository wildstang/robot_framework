package org.wildstang.sample.robot;
import org.ejml.simple.SimpleMatrix;
import org.wildstang.framework.core.Core;
import org.wildstang.sample.subsystems.swerve.SwerveDrive;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;

public class KalmanFilterJenny {
    private final double deltaT = 0.02;
 
    // State vector: [x, vx, ax, y, vy, ay, theta, omega]
    private SimpleMatrix x; // State vector

    // Measurement vector 
    private SimpleMatrix z;

    // Covariance matrix
    private SimpleMatrix P; // State covariance matrix

    // State transition matrix (F)
    private final SimpleMatrix F = new SimpleMatrix(new double[][]{
            {1, deltaT, 0.5 * deltaT * deltaT, 0, 0, 0, 0, 0},
            {0, 1, deltaT, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, deltaT, 0.5 * deltaT * deltaT, 0, 0},
            {0, 0, 0, 0, 1, deltaT, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, deltaT},
            {0, 0, 0, 0, 0, 0, 0, 1}
        });

    // Measurement matrix (H) mapping the state to measurements (x, y, ax, ay, theta)
    private final SimpleMatrix  
        H = new SimpleMatrix(new double[][]{
            {1, 0, 0, 0, 0, 0, 0, 0},  // x
            {0, 0, 0, 1, 0, 0, 0, 0},  // y
            {0, 0, 1, 0, 0, 0, 0, 0},  // ax
            {0, 0, 0, 0, 0, 1, 0, 0},  // ay
            {0, 0, 0, 0, 0, 0, 1, 0}   // theta
        });


    // Process noise covariance (Q)
    // values will be tuned after testing
    private final SimpleMatrix Q = SimpleMatrix.diag(1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4);

    // Measurement noise covariance (R)
    //values will be tuned after testing
    private final SimpleMatrix R = SimpleMatrix.diag(1e-2, 1e-2, 1e-2, 1e-2, 1e-2);

        

    // Identity matrix for update step
    private final SimpleMatrix I = SimpleMatrix.identity(8);

    private SwerveDrive swerve = (SwerveDrive) Core.getSubsystemManager().getSubsystem("SwerveDrive");
    private SwerveDriveOdometry odometry;
    private com.ctre.phoenix.sensors.Pigeon2 gyro;
    private ChassisSpeeds robotSpeeds;

    private final int ACCL_X = 0;
    private final int ACCL_Y = 1;

    public KalmanFilterJenny() {
        odometry = swerve.odometry;
        gyro = swerve.gyro;
        robotSpeeds = swerve.speeds;
        kfInit();
    }

    public void kfInit(){
        initStateMatrix();
        initCovarianceMatrix();
        z = new SimpleMatrix(5, 1);
    }

    private void initStateMatrix(){

        //getting x,y acceleration values from gyro
        short[] shortAcceleration = {0,0,0};
        gyro.getBiasedAccelerometer(shortAcceleration);
        double[] acceleration = new double[2];
        for (int i=0;i<2;i++) {
            acceleration[i] = shortAcceleration[i];
        }
        //geting x,y velocity values from chassisspeeds class
        double currSpeedX = robotSpeeds.vxMetersPerSecond;
        double currSpeedY = robotSpeeds.vyMetersPerSecond;
        
        //initalizing state matrix
        x.set(1,0,odometry.getPoseMeters().getX()); //position in x direction
        x.set(2,0,currSpeedX); //velocity in x direction
        x.set(3,0,acceleration[ACCL_X]); //acceleration in x direction
        x.set(4,0,odometry.getPoseMeters().getY()); //position in y direction
        x.set(4,0,currSpeedY); //velocity in y direction
        x.set(5,0,acceleration[ACCL_Y]); //acceleration in y direction;

    }

    public void initCovarianceMatrix(){
       // Initial state covariance (P) - assuming some initial uncertainty
       P = SimpleMatrix.identity(8).scale(1e-3); // Small initial uncertainty
    }


    private void updateMeasurementMatrix(){
    
        short[] shortAcceleration = {0,0,0};
        gyro.getBiasedAccelerometer(shortAcceleration);
        double[] acceleration = new double[2];
        for (int i=0;i<2;i++) {
            acceleration[i] = shortAcceleration[i];
        }
    
        z.set(0, 0, odometry.getPoseMeters().getX()); // x measurement
        z.set(1, 0, odometry.getPoseMeters().getY()); // y measurement
        z.set(2, 0, acceleration[ACCL_X]); // ax measurement
        z.set(3, 0, acceleration[ACCL_Y]); // ay measurement
        z.set(4, 0, gyro.getYaw()); // theta measurement
    }


    /**
     * Kalman filter prediction step
     */
    private void predict() {
        // Predict the next state: x(t+1) = F * x(t)
        x = F.mult(x);

        // Predict the next state covariance: P(t+1) = F * P * F' + Q
        P = F.mult(P).mult(F.transpose()).plus(Q);
    }

    /**
     * Kalman filter update step
     * @param z Measurement vector [x_meas, y_meas, ax_meas, ay_meas, theta_meas]
     */
    private void update() {
        // Innovation (residual): y = z - H * x
        SimpleMatrix y = z.minus(H.mult(x));

        // Innovation covariance: S = H * P * H' + R
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);

        // Kalman gain: K = P * H' * S^(-1)
        SimpleMatrix K = P.mult(H.transpose()).mult(S.invert());

        // Update the state: x = x + K * y
        x = x.plus(K.mult(y));

        // Update the covariance: P = (I - K * H) * P
        P = I.minus(K.mult(H)).mult(P);
    }

    /**
     * Get the current state estimate
     * @return The current state vector [x, vx, ax, y, vy, ay, theta, omega]
     */
    public SimpleMatrix getState() {
        return x;
    }

    public void kfPeriodic() {

        //get Measurements
        updateMeasurementMatrix();

        // Run the prediction step
        predict();

        // Run the update step 

        update();

        // Get the updated state
        SimpleMatrix updatedState = getState();
       

    }
}