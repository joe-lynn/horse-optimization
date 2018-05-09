package com.myorganization.app.models;

import java.util.ArrayList;

public class RaceInfo {

    private TrackWeather weather;
    private TrackTypes track;
    private TrackRecord trackRecord;
    private ArrayList<Time> fractionalTimes;
    private Time finalTime;
    private ArrayList<Time> splitTimes;
    private int runUp;
    private String winningBreeder;
    private String winningOwner;
    private double totalPool;

    RaceInfo(TrackWeather weather, TrackTypes track, TrackRecord trackRecord, ArrayList<Time> fractionalTimes,
             Time finalTime, ArrayList<Time> splitTimes, int runUp, String winningBreeder,
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

    public ArrayList<Time> getFractionalTimes() {
        return fractionalTimes;
    }

    public Time getFinalTime() {
        return finalTime;
    }

    public ArrayList<Time> getSplitTimes() {
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