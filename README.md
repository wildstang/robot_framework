# WildStang Robot Framework

This is the core framework for WildStang's FRC robot software development.
Example robot code can be found in [examples](examples).
Prior to 2022 the latest version of the robot framework could be found in the latest 20XX_robot_software repo.
This repository is the primary working space for the framework, all future robot software repos should fork it.

This repo contains 2 components to the "framework".
First, it contains the actual robot framework which acts as the control layer of the robot software.
It connects all the pieces together, but does not interact with any hardware on either side.
Second, there is the hardware component, it implements others' APIs to connect the hardware to the framework.
The framework for this reason should not import any external libraries for specific robot functions, such as WPILib.
The framework may import libraries for utility purposes, in this case WPILib's Timer is okay.

## Robot Framework Initialization

In order to create a new code base for a new robot follow these steps:
1. Fork this repo into a 20XX_robot_software repo
2. In `src/main/java/org/wildstang/` duplicate `sample` to `year20XX`
3. Rename package accordingly in each class and `ROBOT_MAIN_CLASS` in `build.gradle`
4. Update `edu.wpi.first.GradleRIO` version in `build.gradle` to latest WPILib version
5. Update `frcYear` in `settings.gradle` to competition year
6. Update `projectYear` in `.wpilib/wpilib_preferences.json` to competition year
7. Update `teamNumber` in `.wpilib/wpilib_preferences.json` if necessary

### Fork Script

The `fork.sh` script automates much of the forking process.
To fork a given branch to a given repo run the following:
```
robot_framework/fork.sh [repo] [branch]
```
Note, if you are looking to fork the framework to a non-WildStang owned repo you must edit the `GITHUB` variable in the script.

The script will automatically update the year across the project if the new repo is named `20XX_...`.
To automatically push these changes append a third argument `push` to the command.

The fork script can also be used to keep your fork up-to-date with the latest changes on a given branch of the framework, simply run:
```
./fork.sh update [branch]
```
Again, to automatically push these changes append a third argument `push` to the command.

## Setting up software
- GitHub Desktop
  - [Download](https://desktop.github.com/)
- VS Code and other required components
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
  - [Download](https://github.com/wpilibsuite/allwpilib/releases/latest/)
- Driver Station and Utilities
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/frc-game-tools.html)
  - [Download](https://www.ni.com/en-us/support/downloads/drivers/download.frc-game-tools.html/)
- RoboRIO Imager
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/imaging-your-roborio.html)
  - Included with Driver Station
- Radio Configuration Utility
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/radio-programming.html)
  - Download link in instructions, look for "FRC Radio Configuration XX.Y.Z"
- REV Hardware Client
  - [Instructions](https://docs.revrobotics.com/rev-hardware-client/getting-started/installation-instructions)
  - [Download](https://github.com/REVrobotics/REV-Software-Binaries/releases/latest)
- Phoenix Tuner (CAN)
  - [Download](https://github.com/CrossTheRoadElec/Phoenix-Releases/releases/latest/)

## Building and deploying to the robot

To build/deploy/debug the robot code either right click on `build.gradle` and choose the desired option or open the command pallete and search and select `WPILib: [FUNCTION] robot code`.
Robot code may also be deployed by pressing `Shift + F5`.

To open the command palette use:
- F1
- Ctrl + Shift + P
- Cmd + Shift + P
- Select the WPILib Command Palette 'W' button in the top right

## Generate Docs

`./gradlew javadoc`

Note: if you have multiple version of the JDK installed you may need to set `JAVA_HOME` specifically to 11.

## External Documentation Links

- [WPILib Docs Site](https://docs.wpilib.org/en/stable/index.html)
- [WPILib Docs PDF](https://readthedocs.org/projects/frc-docs/downloads/pdf/latest/)
- [REV Robotics Docs](https://www.revrobotics.com/software/)
- [CTR Electronics Docs](https://docs.ctre-phoenix.com/en/stable/)
- [navX Docs](https://pdocs.kauailabs.com/navx-mxp/software/roborio-libraries/)
- [Season Materials](https://www.firstinspires.org/resource-library/frc/competition-manual-qa-system)