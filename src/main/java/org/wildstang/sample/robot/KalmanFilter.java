package org.wildstang.sample.robot;
import org.ejml.simple.SimpleMatrix;

public class KalmanFilter {
    private final double deltaT = 0.02;
 
    // State vector: [x, vx, ax, y, vy, ay, theta, omega]
    private SimpleMatrix x; // State vector

    // Covariance matrix
    private SimpleMatrix P; // State covariance matrix

    // State transition matrix (F)
    private final SimpleMatrix F;

    // Measurement matrix (H)
    private final SimpleMatrix H;

    // Process noise covariance (Q)
    private final SimpleMatrix Q;

    // Measurement noise covariance (R)
    private final SimpleMatrix R;

    // Identity matrix for update step
    private final SimpleMatrix I;

    public KalmanFilter() {
        // Initial state (assume starting at origin with zero velocity and acceleration)
        x = new SimpleMatrix(8, 1); // 8x1 state vector initialized to zero

        // Initial state covariance (P) - assuming some initial uncertainty
        P = SimpleMatrix.identity(8).scale(1e-3); // Small initial uncertainty

        // State transition matrix (F)
        F = new SimpleMatrix(new double[][]{
            {1, deltaT, 0.5 * deltaT * deltaT, 0, 0, 0, 0, 0},
            {0, 1, deltaT, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, deltaT, 0.5 * deltaT * deltaT, 0, 0},
            {0, 0, 0, 0, 1, deltaT, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, deltaT},
            {0, 0, 0, 0, 0, 0, 0, 1}
        });

        // Measurement matrix (H) - mapping the state to measurements (x, y, ax, ay, theta)
        H = new SimpleMatrix(new double[][]{
            {1, 0, 0, 0, 0, 0, 0, 0},  // x
            {0, 0, 0, 1, 0, 0, 0, 0},  // y
            {0, 0, 1, 0, 0, 0, 0, 0},  // ax
            {0, 0, 0, 0, 0, 1, 0, 0},  // ay
            {0, 0, 0, 0, 0, 0, 1, 0}   // theta
        });

        // Process noise covariance matrix (Q) - assume small process noise
        Q = SimpleMatrix.diag(1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4, 1e-4);

        // Measurement noise covariance matrix (R) - assume some measurement noise
        R = SimpleMatrix.diag(1e-2, 1e-2, 1e-2, 1e-2, 1e-2);

        // Identity matrix for the update step
        I = SimpleMatrix.identity(8);
    }

    /**
     * Kalman filter prediction step
     */
    public void predict() {
        // Predict the next state: x(t+1) = F * x(t)
        x = F.mult(x);

        // Predict the next state covariance: P(t+1) = F * P * F' + Q
        P = F.mult(P).mult(F.transpose()).plus(Q);
    }

    /**
     * Kalman filter update step
     * @param z Measurement vector [x_meas, y_meas, ax_meas, ay_meas, theta_meas]
     */
    public void update(SimpleMatrix z) {
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

    public static void main(String[] args) {
        // Create a Kalman filter instance
        KalmanFilter kf = new KalmanFilter();

        // Simulate some sensor measurements (these would come from encoders and IMU in reality)
        SimpleMatrix z = new SimpleMatrix(5, 1);
        z.set(0, 0, 2.0); // x measurement
        z.set(1, 0, 1.5); // y measurement
        z.set(2, 0, 0.1); // ax measurement
        z.set(3, 0, 0.05); // ay measurement
        z.set(4, 0, 0.3); // theta measurement

        // Run the prediction step
        kf.predict();

        // Run the update step with the simulated measurements
        kf.update(z);

        // Get the updated state
        SimpleMatrix updatedState = kf.getState();

        // Print the updated state
        System.out.println("Updated state: ");
        updatedState.print();
    }
}


     

