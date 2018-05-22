package com.myorganization.app.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class RaceTracks {
    private static final Logger Log = LogManager.getLogger("RaceTracks");
    private static final Map<String, String> tracks = new HashMap<>();

    static {
        tracks.put("AQE", "Aqueduct");
        tracks.put("SA", "Santa Anita Park");
        tracks.put("BEL", "Belmont Park");
        tracks.put("GP", "Gulfstream Park");
    }

    public static String getTrackCode(String track) {
        String code = "";
        for (Map.Entry<String, String> entry : tracks.entrySet()) {
            if (StringUtils.equalsIgnoreCase(entry.getValue(), track)) {
                code = entry.getKey();
                break;
            }
        }

        if (code.length() == 0) {
            Log.error("Failed to get Race Track for name: " + track);
            return null;
        } else {
            return code;
        }
    }

    public static String getTrack(String trackCode) {
        String raceTrack = tracks.get(trackCode);
        if (raceTrack == null) {
            Log.error("Failed to get Race Track for code: " + trackCode);
        }
        return raceTrack;
    }
}