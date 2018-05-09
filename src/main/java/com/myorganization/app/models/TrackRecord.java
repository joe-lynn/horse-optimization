package com.myorganization.app.models;

import java.util.Date;

public class TrackRecord {

    private Horse horse;
    private Time record;
    private Date date;

    TrackRecord(Horse horse, Time time, Date date) {
        this.horse = horse;
        this.record = time;
        this.date = date;
    }
}