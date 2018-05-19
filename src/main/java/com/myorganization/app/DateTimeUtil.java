package com.myorganization.app;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {


    private static DateTimeFormatter threePartTimeFormatter = DateTimeFormat.forPattern("mm:ss.SS");
    private static DateTimeFormatter twoPartTimeFormatter = DateTimeFormat.forPattern("ss.SS");

    private static DateTimeFormatter shortPartTimeFormatter = DateTimeFormat.forPattern("ss:SS");

    /**
     * Used to cover both cases of Fractional Times where minutes exists, and when they don't.
     * Ex with minutes: "1:41.25"
     * Ex w/o minutes: "48.20"
     * @param timeString - The raw stopwatch string value
     * @return A correctly parsed LocalTime version of the string parameter given
     */
    public static LocalTime parseStopWatchString(String timeString) {
        if (timeString.length() > 5) {
            return LocalTime.parse(timeString, threePartTimeFormatter);
        } else {
            return LocalTime.parse(timeString, twoPartTimeFormatter);
        }
    }

    /**
     * Used for mainly to parse Split Times in the format ex. "(24:29)" into LocalTime
     * @param timeString - The raw stopwatch time string
     * @return Parsed LocalTime version of the raw string parameter, timeString
     */
    public static LocalTime parseShortStopWatchString(String timeString) {
        timeString = timeString.replace("(", "").replace(")", "");
        return LocalTime.parse(timeString, shortPartTimeFormatter);
    }

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM d, yyyy");

    public static LocalDate parseDateString(String dateString) {
        return LocalDate.parse(dateString, dateFormatter);
    }
}