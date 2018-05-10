package com.myorganization.app.models;

import java.time.LocalDate;

public class RaceEntry {
    private String trackCode;
    private LocalDate date;
    private int raceNum;
    private RaceInfo raceInfo;
    private int weight;
//    private ME me;
    private int pp;
    private PositionData positionData;
    private double odds;
    private String comments;

    public RaceEntry(String trackCode, LocalDate date, int raceNum, RaceInfo raceInfo, int weight, int pp, PositionData positionData, double odds, String comments) {
        this.trackCode = trackCode;
        this.date = date;
        this.raceNum = raceNum;
        this.raceInfo = raceInfo;
        this.weight = weight;
        this.pp = pp;
        this.positionData = positionData;
        this.odds = odds;
        this.comments = comments;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getRaceNum() {
        return raceNum;
    }

    public RaceInfo getRaceInfo() {
        return raceInfo;
    }

    public int getWeight() {
        return weight;
    }

    public int getPp() {
        return pp;
    }

    public PositionData getPositionData() {
        return positionData;
    }

    public double getOdds() {
        return odds;
    }

    public String getComments() {
        return comments;
    }
}