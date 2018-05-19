package com.myorganization.app.models;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TrackRecord {

    private Horse horse;
    private LocalTime record;
    private LocalDate date;

    public TrackRecord(Horse horse, LocalTime time, LocalDate date) {
        this.horse = horse;
        this.record = time;
        this.date = date;
    }

    @Override
    public String toString() {
        return "TrackRecord{" +
                "horse=" + horse +
                ", record=" + record +
                ", date=" + date +
                '}';
    }
}