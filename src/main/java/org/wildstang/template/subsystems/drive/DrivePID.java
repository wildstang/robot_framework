package org.wildstang.template.subsystems.drive;

import org.wildstang.framework.pid.PIDConstants;

/**
 * Used for configuring drive PID constants.
 * Note: only four slots are available on the Talon.
 */
public enum DrivePID {

    PATH_POS(0, new PIDConstants(0.0652, 1.00, 0.0, 0.0)),
    PATH_HEAD(1, new PIDConstants(0.0, 1.00, 0.0, 10)),
    BASE_LOCK(2, new PIDConstants(0.0, .8, 0.001, 10));

    private PIDConstants k;
    private int slot;

    DrivePID(int slot, PIDConstants pid) {
        this.slot = slot;
        this.k = pid;
    }

    public PIDConstants getConstants() {
        return k;
    }

    public int getSlot() {
        return slot;
    }
}