package com.myorganization.app.models;

import jregex.Matcher;

public class DebugDataObject {

    private String line;
    private Matcher dataMatcher;
    private boolean hasThreeQuarter;

    public DebugDataObject(String line, Matcher dataMatcher, boolean hasThreeQuarter) {
        this.line = line;
        this.dataMatcher = dataMatcher;
        this.hasThreeQuarter = hasThreeQuarter;
    }

    public String getLine() {
        return line;
    }

    public Matcher getDataMatcher() {
        return dataMatcher;
    }

    public boolean isThreeQuarter() {
        return hasThreeQuarter;
    }
}