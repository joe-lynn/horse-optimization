package com.myorganization.app.models;

import java.util.concurrent.TimeUnit;

public class Time {

    private long time;

    Time(long elapsed) {
        this.time = elapsed;
    }

    public String formatTime()
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(this.time);
        final long min = TimeUnit.MILLISECONDS.toMinutes(this.time - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(this.time - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(this.time - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }
}