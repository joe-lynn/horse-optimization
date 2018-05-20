package com.myorganization.app.models;

import java.util.HashMap;
import java.util.Map;

public class TrackLength {
    private static final Map<String, Integer> tracks = new HashMap<>();

    // TODO: Use track length to determine how many fractional times there should be: https://www.equibase.com/newfan/fractional_times.cfm
    static {
        tracks.put("Six Furlongs On The Inner track", 3);
        tracks.put("Six Furlongs On The Dirt", 3);
        tracks.put("Six Furlongs On The Turf", 3);
        tracks.put("Six And One Half Furlongs On The Dirt", 3);
        tracks.put("One Mile On The Dirt", 3);
        tracks.put("One Mile On The Inner track", 3);
        tracks.put("One Mile On The Turf", 4);
        tracks.put("One And One Sixteenth Miles On The Inner track", 4);
        tracks.put("One And One Sixteenth Miles On The Dirt", 4);
        tracks.put("One And One Eighth Miles On The Inner turf", 4);
    }

    public static int getNumOfFractionalTimes(String trackLength) {
        return tracks.get(trackLength);
    }
}