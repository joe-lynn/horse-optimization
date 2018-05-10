package com.myorganization.app.models;

import org.joda.time.LocalTime;
import java.util.ArrayList;

public class RaceInfo {

    private TrackWeather weather;
    private TrackTypes track;
    private TrackRecord trackRecord;
    private ArrayList<LocalTime> fractionalTimes;
    private LocalTime finalTime;
    private ArrayList<LocalTime> splitTimes;
    private int runUp;
    private String winningBreeder;
    private String winningOwner;
    private double totalPool;

    RaceInfo(TrackWeather weather, TrackTypes track, TrackRecord trackRecord, ArrayList<LocalTime> fractionalTimes,
             LocalTime finalTime, ArrayList<LocalTime> splitTimes, int runUp, String winningBreeder,
             String winningOwner, double totalPool) {
        this.weather = weather;
        this.track = track;
        this.trackRecord = trackRecord;
        this.fractionalTimes = fractionalTimes;
        this.finalTime = finalTime;
        this.splitTimes = splitTimes;
        this.runUp = runUp;
        this.winningBreeder = winningBreeder;
        this.winningOwner = winningOwner;
        this.totalPool = totalPool;
    }

    public TrackWeather getWeather() {
        return weather;
    }

    public TrackTypes getTrack() {
        return track;
    }

    public TrackRecord getTrackRecord() {
        return trackRecord;
    }

    public ArrayList<LocalTime> getFractionalTimes() {
        return fractionalTimes;
    }

    public LocalTime getFinalTime() {
        return finalTime;
    }

    public ArrayList<LocalTime> getSplitTimes() {
        return splitTimes;
    }

    public int getRunUp() {
        return runUp;
    }

    public String getWinningBreeder() {
        return winningBreeder;
    }

    public String getWinningOwner() {
        return winningOwner;
    }

    public double getTotalPool() {
        return totalPool;
    }
}