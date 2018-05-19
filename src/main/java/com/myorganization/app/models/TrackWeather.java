package com.myorganization.app.models;

import java.util.HashMap;
import java.util.Map;

public enum TrackWeather {

    CLOUDY("Cloudy"), CLEAR("Clear"), SHOWERY("Showery");

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
        return lookup.get(abbrev);
    }
}