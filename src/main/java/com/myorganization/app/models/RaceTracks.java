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
        tracks.put("AQU", "Aqueduct");
        tracks.put("BEL", "Belmont Park");
        tracks.put("NP", "Century Downs"); // What is this place
        tracks.put("PRV", "Crooked River Roundup");
        tracks.put("DEP", "Desert Park");
        tracks.put("DAY", "Dayton");
        tracks.put("DMR", "Del Mar");
        tracks.put("DEL", "Delaware Park");
        tracks.put("DED", "Delta Downs");
        tracks.put("DXD", "Dixie Downs");
        tracks.put("UN", "Eastern Oregon Livestock Show"); // Code not right
        tracks.put("ELK", "Elko County Fair");
        tracks.put("CD", "Ellis Park");
        tracks.put("EMD", "Emerald Downs");
        tracks.put("EMT", "Emmett");
        tracks.put("EUR", "Eureka");
        tracks.put("EVD", "Evangeline Downs");
        tracks.put("FG", "Fair Grounds");
        tracks.put("FAI", "Fair Hill");
        tracks.put("FMT", "Fair Meadows");
        tracks.put("GP", "Gulfstream Park");
        tracks.put("MVR", "Mahoning Valley Race Course");
        tracks.put("MAL", "Malvern");
        tracks.put("MAN", "Manor Downs");
        tracks.put("MAF", "Marias Fair");
        tracks.put("MOR", "Morven Park");
        tracks.put("MNR", "Mountaineer Casino Racetrack & Resort");
        tracks.put("MPM", "Mt. Pleasant Meadows");
        tracks.put("FAR", "North Dakota Horse Park");
        tracks.put("SA", "Santa Anita Park");
        tracks.put("TDN", "Thistledown");
        tracks.put("TIL", "Tillamook County Fair");
        tracks.put("TIM", "Timonium");
        tracks.put("TRY", "Tryon");
        tracks.put("TUP", "Turf Paradise");
        tracks.put("TP", "Turfway Park");
        tracks.put("WTS", "Waistburg Race Track");
        tracks.put("WW", "Walla Walla");
        tracks.put("WBR", "Weber Downs");
        tracks.put("WYO", "Wyoming Downs");
        tracks.put("YM", "Yakima Meadows");
        tracks.put("YAV", "Yavapai Downs");
        tracks.put("YD", "Yellowstone Downs");
        tracks.put("ZIA", "Zia Park");
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