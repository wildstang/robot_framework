package org.wildstang.template.auto.Steps;

import org.wildstang.framework.auto.AutoStep;
import org.wildstang.template.robot.WsSubsystems;
import org.wildstang.template.subsystems.swerve.SwerveDrive;
import org.wildstang.template.subsystems.targeting.WsVision;

/**
 * AutoStep used to enable/disable AprilTag alignment
 */
public class TagOnStep extends AutoStep {

    private SwerveDrive swerve;
    private WsVision limelight;
    private boolean isBlue, on;

    public TagOnStep(boolean isOn, boolean isBlueAlliance) {
        isBlue = isBlueAlliance;
        on = isOn;
    }

    @Override
    public void update() {
        swerve.setAutoTag(on, isBlue);
        limelight.setGamePiece(false);
        this.setFinished();
    }

    @Override
    public void initialize() {
        swerve = (SwerveDrive) WsSubsystems.SWERVE_DRIVE.get();
        limelight = (WsVision) WsSubsystems.WS_VISION.get();
    }

    @Override
    public String toString() {
        return "Tag Align On";
    }

}
