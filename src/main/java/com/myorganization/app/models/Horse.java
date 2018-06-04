package com.myorganization.app.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Horse implements Serializable{

    private String name;
    private transient ArrayList<RaceEntry> raceEntries;

    public Horse(String name) {
        this.name = name;
        this.raceEntries = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public boolean addRaceEntry(RaceEntry entry) {
        return this.raceEntries.add(entry);
    }

    @Override
    public String toString() {
        return "Horse{" +
                "name='" + name + '\'' +
                ", raceEntries=" + raceEntries +
                '}';
    }
}