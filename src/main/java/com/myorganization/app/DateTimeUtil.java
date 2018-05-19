package com.myorganization.app;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {


    private static DateTimeFormatter threePartTimeFormatter = DateTimeFormat.forPattern("mm:ss.SS");
    private static DateTimeFormatter twoPartTimeFormatter = DateTimeFormat.forPattern("ss.SS");
    
    public static LocalTime parseStopWatchString(String timeString) {
        if (timeString.length() > 5) {
            return LocalTime.parse(timeString, threePartTimeFormatter);
        } else {
            return LocalTime.parse(timeString, twoPartTimeFormatter);
        }
    }

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM d, yyyy");

    public static LocalDate parseDateString(String dateString) {
        return LocalDate.parse(dateString, dateFormatter);
    }
}