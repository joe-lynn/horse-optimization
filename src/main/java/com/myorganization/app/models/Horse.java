package com.myorganization.app.models;

import java.util.ArrayList;

public class Horse {

    private String name;
    private ArrayList<RaceEntry> raceEntries;

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
                '}';
    }
}