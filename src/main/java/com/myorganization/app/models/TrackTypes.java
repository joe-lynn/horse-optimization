package com.myorganization.app.models;

import java.util.HashMap;
import java.util.Map;

public enum TrackTypes {
    
    SLOPPY("Sloppy (Sealed)"), FAST("Fast"), FIRM("Firm");

    private final String abbreviation;

    private static final Map<String, TrackTypes> lookup = new HashMap<>();

    static {
        for (TrackTypes w : TrackTypes.values()) {
            lookup.put(w.getAbbreviation(), w);
        }
    }

    TrackTypes(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static TrackTypes get(String abbrev) {
        return lookup.get(abbrev);
    }
}