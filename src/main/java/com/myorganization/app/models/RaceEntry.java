package com.myorganization.app.models;


import org.joda.time.LocalDate;

public class RaceEntry {
    private String trackCode;
    private LocalDate date;
    private String pgm;
    private String jockey;
    private int raceNum;
    private RaceInfo raceInfo;
    private int weight;
    private String me;
    private int pp;
    private PositionData positionData;
    private double odds;
    private String comments;

    public RaceEntry(String trackCode, LocalDate date, String pgm, String jockey, int raceNum, RaceInfo raceInfo, int weight, String me, int pp, PositionData positionData, double odds, String comments) {
        this.trackCode = trackCode;
        this.date = date;
        this.pgm = pgm;
        this.jockey = jockey;
        this.raceNum = raceNum;
        this.raceInfo = raceInfo;
        this.weight = weight;
        this.me = me;
        this.pp = pp;
        this.positionData = positionData;
        this.odds = odds;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "RaceEntry{" +
                "trackCode='" + trackCode + '\'' +
                ", date=" + date +
                ", pgm=" + pgm +
                ", jockey=" + jockey +
                ", raceNum=" + raceNum +
                ", weight=" + weight +
                ", me='" + me + '\'' +
                ", pp=" + pp +
                ", positionData=" + positionData +
                ", odds=" + odds +
                ", comments='" + comments + '\'' +
                '}';
    }
}