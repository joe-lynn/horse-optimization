package com.myorganization.app.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public enum TrackTypes {

    SLOPPY("Sloppy"), MUDDY("Muddy"), SLOPPY_SEALED("Sloppy (Sealed)"), MUDDY_SEALED("Muddy (Sealed)"),
    FAST("Fast"), FAST_SEALED("Fast (Sealed)"), FIRM("Firm"), FIRM_SEALED("Firm (Sealed)"),
    YIELDING("Yielding"), YIELDING_SEALED("Yielding (Sealed)"), GOOD("Good"), GOOD_SEALED("Good (Sealed)"),
    SOFT("Soft"), SOFT_SEALED("Soft (Sealed)"), WET_FAST("Wet Fast"), WEST_FAST_SEALED("Wet Fast (Sealed)"),
    SLOW("Slow"), SLOW_SEALED("Slow (Sealed)");

    private static final Logger Log = LogManager.getLogger("TrackTypes");

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
        TrackTypes trackTypes = lookup.get(abbrev);
        if (trackTypes == null) {
            Log.error("Failed to get track type for: " + abbrev);
        }
        return trackTypes;
    }
}