package com.myorganization.app.models;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RaceTracks {
    private static final Map<String, String> tracks = new HashMap<>();

    static {
        tracks.put("AQE", "Aqueduct");
        tracks.put("BEL", "Blemont");
        tracks.put("DC", "Churchill Downs");
        tracks.put("GP", "Gulfstream Park");
        tracks.put("MTH", "Monmouth Park");
        tracks.put("SA", "Santa Anita");
        tracks.put("WO", "Woodbine");
        tracks.put("AP", "Arlington Park");
        tracks.put("SAR", "Saratoga");
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