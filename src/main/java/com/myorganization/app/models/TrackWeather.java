package com.myorganization.app.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public enum TrackWeather {

    CLOUDY("Cloudy"), CLEAR("Clear"), SHOWERY("Showery"), RAINY("Rainy"), FOGGY("Foggy"), SNOWING("Snowing"), HAZY("Hazy");

    private static final Logger Log = LogManager.getLogger("TrackWeather");

    private final String abbreviation;

    private static final Map<String, TrackWeather> lookup = new HashMap<>();

    static {
        for (TrackWeather w : TrackWeather.values()) {
            lookup.put(w.getAbbreviation(), w);
        }
    }

    TrackWeather(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static TrackWeather get(String abbrev) {
        TrackWeather trackWeather = lookup.get(abbrev);
        if (trackWeather == null) {
            Log.error("Failed to get track weather for: " + abbrev);
        }
        return trackWeather;
    }
}