package com.myorganization.app;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final Logger Log = LogManager.getLogger("DateTimeUtil");

    private static DateTimeFormatter threePartTimeFormatter = DateTimeFormat.forPattern("mm:ss.SS");
    private static DateTimeFormatter twoPartTimeFormatter = DateTimeFormat.forPattern("ss.SS");

    private static DateTimeFormatter threeColonPartTimeFormatter = DateTimeFormat.forPattern("mm.ss:SS");
    private static DateTimeFormatter twoColonPartTimeFormatter = DateTimeFormat.forPattern("ss:SS");

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
        timeString = timeString.trim().replace("(", "").replace(")", "");
        if (timeString.length() > 5) {
            return LocalTime.parse(timeString, threeColonPartTimeFormatter);
        } else {
            return LocalTime.parse(timeString, twoColonPartTimeFormatter);
        }
    }

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM d, yyyy");
    private static DateTimeFormatter dateNoSpaceFormatter = DateTimeFormat.forPattern("MMMM d,yyyy");

    public static LocalDate parseDateString(String dateString) {
        if (StringUtils.countMatches(dateString, " ") == 2) {
            return LocalDate.parse(dateString, dateFormatter);
        } else if (StringUtils.countMatches(dateString, " ") == 1) {
            return LocalDate.parse(dateString, dateNoSpaceFormatter);
        } else {
            Log.error("Failed to detect how to parse Date String: " + dateString);
            return null;
        }
    }
}