# WildStang Robot Framework

The WildStang Robotics Program's robot framework has evolved from over 10 years of Java software development.
It is the foundation of each robot, providing hardware enumerations, autonomous handling, and common utilities.

## Helpful Link

### Software Packages

- GitHub Desktop
  - [Download](https://desktop.github.com/download)
- VS Code and other required components
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
  - [Download](https://github.com/wpilibsuite/allwpilib/releases/latest/)
- Driver Station and Utilities
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/frc-game-tools.html)
  - [Download](https://www.ni.com/en-us/support/downloads/drivers/download.frc-game-tools.html/)
- RoboRIO Imager
  - [Instructions](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/imaging-your-roborio.html)
  - Included with Driver Station
- Radio Configuration
  - [Instructions](https://frc-radio.vivid-hosting.net/overview/quick-start-guide)
- REV Hardware Client
  - [Instructions & Download](https://docs.revrobotics.com/rev-hardware-client/gs/install)
- Phoenix 6
  - [Instructions](https://v6.docs.ctr-electronics.com/en/stable/docs/installation/installation-frc.html)
  - [Download](https://github.com/CrossTheRoadElec/Phoenix-Releases/releases/latest)

### External Documentation

- [Season Materials](https://www.firstinspires.org/resource-library/frc/competition-manual-qa-system)
- [WPILib Docs Site](https://docs.wpilib.org/en/stable/index.html)
- [WPILib Docs PDF](https://readthedocs.org/projects/frc-docs/downloads/pdf/stable/)
- [REV Robotics Docs](https://docs.revrobotics.com/)
- [Phoenix 6 Docs](https://v6.docs.ctr-electronics.com/en/stable/)

## Building and deploying to the robot

To build/deploy/debug the robot code either right click on `build.gradle` and choose the desired option or open the command pallete and search and select `WPILib: [FUNCTION] robot code`.
Robot code may also be deployed by pressing `Shift + F5`.

To open the command palette use:
- F1
- Ctrl + Shift + P
- Cmd + Shift + P
- Select the WPILib Command Palette 'W' button in the top right

### Dev Container

If running on a system with Podman or Docker installed, the included [Dev Container](https://code.visualstudio.com/docs/devcontainers/containers) can be used instead of installing dependencies.
This container is based off Microsoft's Java image and will automatically install the latest WPILib extension.
VS Code should automatically detect the Dev Container and ask if you would like to use it.
If you don't get this pop-up open the Command Pallet and select `Dev Containers: Reopen in Container`.
If you don't see this option you may need to install the [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers).

## Robot Framework Initialization

In order to create a new code base for a new robot follow these steps:
1. Fork this repo into a 20XX_robot_software repo
2. In `src/main/java/org/wildstang/` duplicate `sample` to `year20XX`
3. Rename package accordingly in each class and `ROBOT_MAIN_CLASS` in `build.gradle`
4. Update `edu.wpi.first.GradleRIO` version in `build.gradle` to latest WPILib version
5. Update `frcYear` in `settings.gradle` to competition year
6. Update `projectYear` in `.wpilib/wpilib_preferences.json` to competition year
7. Update `teamNumber` in `.wpilib/wpilib_preferences.json` if necessary

## Other Scripts

### Generate Docs

`./gradlew javadoc`

Note: if you have multiple version of the JDK installed you may need to set `JAVA_HOME` specifically to 11.

### Fork Script

The `scripts/fork.sh` script automates much of the forking process.
To fork a given branch to a given repo run the following:
```
robot_framework/scripts/fork.sh [repo] [branch]
```
Note, if you are looking to fork the framework to a non-WildStang owned repo you must edit the `GITHUB` variable in the script.

The script will automatically update the year across the project if the new repo is named `20XX_...`.
To automatically push these changes append a third argument `push` to the command.

### Pull Upstream

The `scripts/pull-upstream.sh` script automatically pulls in changes from an upstream repository.
This can be used to pull in changes after the repo is forked.
```
scripts/pull-upstream.sh [repo] [branch]
```

### Push Upstream

The `scripts/push-upstream.sh` script automatically pushes in changes from the current repo to an upstream repository.
```
scripts/push-upstream.sh [repo] [branch]
```

## Github Actions

### Robot CI

Automatically builds the project everytime a commit is pushed.
No setup is required.

### Public Sync

Automatically pushes the contents of the `public` branch to the corresponding public repo.
That corresponding repo is `wildstang/YEAR_TEAM_robot_software` where `YEAR` and `TEAM` are read from `.wpilib/wpilib_preferences.json`.
This repo requires a personal access token to be added as a repository secret `PAT`.
Repository secrets are found in `Settings > Secrets and variables > Actions`.

### WPILib Update

Automatically updates the WPILib version used for the repo daily at 1:11.
This is really only intended to be used on the robot_framework repo.
This repo requires a personal access token to be added as a repository secret `UPDATE_PAT`.
Repository secrets are found in `Settings > Secrets and variables > Actions`.
