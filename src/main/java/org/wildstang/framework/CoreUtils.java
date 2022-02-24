package org.wildstang.framework;

/**
 * Contains utility functions used across the framework.
 */
public class CoreUtils {

    /**
     * Throws a NullPointerException if p_param is null.
     * @param p_param Object to check if null.
     * @param p_message Message to use in exception.
     * @throws NullPointerException If p_param is null.
     */
    public static void checkNotNull(Object p_param, String p_message) {
        if (p_param == null) {
            throw new NullPointerException(p_message);
        }
    }

    // /**
    //  * Reads a drive path from a class and creates a Trajectory.
    //  * @param p_path String name of Path on BobTrajectory.
    //  * @param isForwards True if the path is read forwards.
    //  * @return A Trajectory representing all the CTRE TrajectoryPoints.
    //  */
    // public static Trajectory readTrajectory(String p_path) {
    //     Reader in;
    //     List<CSVRecord> records;
    //     try {
    //         in = new FileReader(p_path);
    //         records = CSVFormat.EXCEL.withHeader("Delta Time","X Point","Y Point","Position","Velocity","Acceleration","Jerk","Heading").parse(in).getRecords();
    //     }
    //     catch (Exception e) {
    //         for (int i = 0; i < 100; ++i) {
    //             System.out.println("FAILED TO READ PATH!");
    //         }
    //         return new Trajectory();
    //     }
    //     if (records.get(0).get("Delta Time").equals("dt") || records.get(0).get("Delta Time").equals("Delta Time")){
    //         records.remove(0);
    //     }
        
    //     int trajectoryModifier = isForwards ? 1 : -1;

    //     ArrayList<TrajectoryPoint> trajPoints = new ArrayList<TrajectoryPoint>();
    //     double[][] dataPoints = new double[records.size()][3];
    //     Trajectory trajectory = new Trajectory();
    //     TrajectoryPoint mpPoint = null;

    //     int i = 0;
    //     for (CSVRecord record : records) {
    //         mpPoint = new TrajectoryPoint();
    //         dataPoints[i] = new double[3];

    //         dataPoints[i][0] = Double.parseDouble(record.get("Delta Time"));
    //         dataPoints[i][1] = trajectoryModifier * Double.parseDouble(record.get("Position")) * ticksPerInchMod/12;//*8.923;
    //         dataPoints[i][2] = trajectoryModifier * Double.parseDouble(record.get("Velocity")) * ticksPerInchMod/10/12;//*8.923;

    //         mpPoint.timeDur = (int) dataPoints[i][0];
    //         mpPoint.position = dataPoints[i][1];
    //         mpPoint.velocity = dataPoints[i][2];

    //         mpPoint.profileSlotSelect0 = 0;

    //         mpPoint.zeroPos = i == 0;
    //         mpPoint.isLastPoint = i == records.size() - 1;

    //         trajPoints.add(mpPoint);
    //         ++i;
    //     }

    //     trajectory.setTalonPoints(trajPoints);

    //     return trajectory;
    // }
}