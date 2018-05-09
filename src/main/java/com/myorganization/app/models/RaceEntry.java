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
    private int startPosition;
    private int quarterPosition;
    private int halfPosition;
    private int strPosition;
    private int finishPosition;
    private double odds;
    private String comments;

    public RaceEntry(String trackCode, LocalDate date, int raceNum, RaceInfo raceInfo, int weight, int pp, int startPosition, int quarterPosition, int halfPosition, int strPosition, int finishPosition, double odds, String comments) {
        this.trackCode = trackCode;
        this.date = date;
        this.raceNum = raceNum;
        this.raceInfo = raceInfo;
        this.weight = weight;
        this.pp = pp;
        this.startPosition = startPosition;
        this.quarterPosition = quarterPosition;
        this.halfPosition = halfPosition;
        this.strPosition = strPosition;
        this.finishPosition = finishPosition;
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

    public int getStartPosition() {
        return startPosition;
    }

    public int getQuarterPosition() {
        return quarterPosition;
    }

    public int getHalfPosition() {
        return halfPosition;
    }

    public int getStrPosition() {
        return strPosition;
    }

    public int getFinishPosition() {
        return finishPosition;
    }

    public double getOdds() {
        return odds;
    }

    public String getComments() {
        return comments;
    }
}