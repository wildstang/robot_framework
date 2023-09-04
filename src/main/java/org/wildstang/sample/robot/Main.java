package org.wildstang.sample.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do NOT modify this file. This is the main class as defined in build.gradle.
 * All robot code execution starts here by calling Main.main().
 */
public final class Main {

  /**
   * Main initialization function.
   * The TimedRobot defined in Robot.java is started from here.
   *
   * @param args No arguments are expected.
   */
  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
