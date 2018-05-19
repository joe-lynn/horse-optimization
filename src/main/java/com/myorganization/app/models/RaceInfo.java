package com.myorganization.app.models;

import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.util.List;

public class RaceInfo {

    private TrackWeather weather;
    private TrackTypes track;
    private TrackRecord trackRecord;
    private List<LocalTime> fractionalTimes;
    private LocalTime finalTime;
    private List<LocalTime> splitTimes;
    private int runUp;
    private String winningBreeder;
    private String winningOwner;
    private BigDecimal totalPool;

    public RaceInfo(TrackWeather weather, TrackTypes track, TrackRecord trackRecord, List<LocalTime> fractionalTimes,
                    LocalTime finalTime, List<LocalTime> splitTimes, int runUp, String winningBreeder,
                    String winningOwner, BigDecimal totalPool) {
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

    public List<LocalTime> getFractionalTimes() {
        return fractionalTimes;
    }

    public LocalTime getFinalTime() {
        return finalTime;
    }

    public List<LocalTime> getSplitTimes() {
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

    public BigDecimal getTotalPool() {
        return totalPool;
    }

    @Override
    public String toString() {
        return "RaceInfo{" +
                "weather=" + weather +
                ", track=" + track +
                ", fractionalTimes=" + fractionalTimes +
                ", finalTime=" + finalTime +
                ", splitTimes=" + splitTimes +
                ", runUp=" + runUp +
                ", winningBreeder='" + winningBreeder + '\'' +
                ", winningOwner='" + winningOwner + '\'' +
                ", totalPool=" + totalPool +
                '}';
    }
}