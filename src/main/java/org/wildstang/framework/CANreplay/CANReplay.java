package org.wildstang.framework.CANreplay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.util.datalog.DataLogIterator;
import edu.wpi.first.util.datalog.DataLogReader;
import edu.wpi.first.util.datalog.DataLogRecord;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class CANReplay {
    private final String filename;
    private boolean isValid;

    private DataLogReader reader;
    private DataLogIterator iterator;
    private DataLogRecord currentRecord;

    private Map<Integer, String> replayData;

    public CANReplay(String filename) {
        this.filename = filename;
    }

    public void init() {
        // Open log file
        try {
            reader = new DataLogReader(filename);
        } catch (IOException e) {
            DriverStation.reportError("[AdvantageKit] Failed to open replay log file.", true);
        }

        // Check validity
        if (!reader.isValid()) {
            DriverStation.reportError("[AdvantageKit] The replay log is not a valid WPILOG file.", false);
            isValid = false;
        } else {
            isValid = true;
        }

        // Create iterator and reset
        iterator = reader.iterator();
        replayData = new HashMap<>();
    }

    public void update() {
        if (!isValid) return;

        while (currentRecord.getTimestamp() < Timer.getTimestamp()) {
            System.out.println(currentRecord.getStartData().type);
            currentRecord = iterator.next();
        }
    }
}
