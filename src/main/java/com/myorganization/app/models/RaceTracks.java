package com.myorganization.app.models;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RaceTracks {
    private static final Map<String, String> tracks = new HashMap<>();

    static {
        tracks.put("AQE", "Aqueduct");
        tracks.put("SA", "Santa Anita Park");
        tracks.put("BEL", "Belmont Park");
    }

    public static String getTrackCode(String track) {
        String code = "";
        for (Map.Entry<String, String> entry : tracks.entrySet()) {
            if (StringUtils.equalsIgnoreCase(entry.getValue(), track)) {
                code = entry.getKey();
                break;
            }
        }
        return code.length() == 0 ? null : code;
    }

    public static String getTrack(String trackCode) {
        return tracks.get(trackCode);
    }
}