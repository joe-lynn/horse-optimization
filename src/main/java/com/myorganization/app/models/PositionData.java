package com.myorganization.app.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PositionData {
    private static final Logger Log = LogManager.getLogger("PositionData");

    private int startPosition;
    private int quarterPosition;
    private int halfPosition;
    private int strPosition;
    private int threeQuarterPosition;
    private int finishPosition;

    private boolean hasThreeQuarter;

    private static final String fiveRegex = "(\\d+)[0-9a-zA-Z~]* (\\d+)[0-9a-zA-Z~]* (\\d+)[0-9a-zA-Z~]* (\\d+)[0-9a-zA-Z~]* (\\d+)";
    private static final String fourRegex = "(\\d+)[0-9a-zA-Z~]* (\\d+)[0-9a-zA-Z~]* (\\d+)[0-9a-zA-Z~]* (\\d+)";
    private static final Pattern fivePattern = Pattern.compile(fiveRegex, Pattern.MULTILINE);
    private static final Pattern fourPattern = Pattern.compile(fourRegex, Pattern.MULTILINE);

    /**
     * Constructor used to automatically parse a given string of horse performance data for a single row.
     * First the rawPositionDataString (ex. "63 51 41/2 3Head", or the 1/4 1/2 Str Fin columns with optional 3/4) is sanitized of common annoying superscripts.
     * Depending on if there is an optional 5th column, 3/4 or hasThreeQuarter, we will use regex to parse the remaining sanitized string.
     * @param startPosition - The already parsed, Start column of horse data table
     * @param rawPositionDataString - The raw OCR string of the more complex horse position data, with superscripts to be removed and positions to be parsed
     * @param hasThreeQuarter - When true, this horse position data row in the table has 5 columns (the optional 3/4 column), instead of 4
     */
    public PositionData(int startPosition, String rawPositionDataString, boolean hasThreeQuarter) {
        // Store the easy ones
        this.startPosition = startPosition;
        this.hasThreeQuarter = hasThreeQuarter;
        this.threeQuarterPosition = -1;

        // Remove a bunch of common erroneous superscripts
        rawPositionDataString = rawPositionDataString.replace("1/2", "");
        rawPositionDataString = rawPositionDataString.replace("1/4", "");
        rawPositionDataString = rawPositionDataString.replace("2/4", "");
        rawPositionDataString = rawPositionDataString.replace("3/4", "");
        rawPositionDataString = rawPositionDataString.replace("Head", "");
        rawPositionDataString = rawPositionDataString.replace("Neck", "");
        rawPositionDataString = rawPositionDataString.replace("Nose", "");
        rawPositionDataString = rawPositionDataString.replace("  ", " ");
        rawPositionDataString = rawPositionDataString.replace("   ", " ");

        // Important: If horse gets "---" time, we represent that internally with a 0
        if (rawPositionDataString.contains("---")) {
            rawPositionDataString = rawPositionDataString.replace("---", "0");
        }

        if (rawPositionDataString.length() < 7) {
            Log.error("WHY YOU SO SMALL: " + rawPositionDataString);
        }

        // Some have 3/4 column, and some do, hasThreeQuarter is flag to determine when it does and doesn't
        if (hasThreeQuarter) {
            Matcher matcher = fivePattern.matcher(rawPositionDataString);
            if (matcher.find()) {
                assert matcher.groupCount() == 5;
                this.quarterPosition = Integer.parseInt(matcher.group(1));
                this.halfPosition = Integer.parseInt(matcher.group(2));
                this.strPosition = Integer.parseInt(matcher.group(3));
                this.threeQuarterPosition = Integer.parseInt(matcher.group(4));
                this.finishPosition = Integer.parseInt(matcher.group(5));
            } else {
                Log.error("Match not found for: '" + rawPositionDataString);
            }
        } else {
            Matcher matcher = fourPattern.matcher(rawPositionDataString);
            if (matcher.find()) {
                assert matcher.groupCount() == 4;
                this.quarterPosition = Integer.parseInt(matcher.group(1));
                this.halfPosition = Integer.parseInt(matcher.group(2));
                this.strPosition = Integer.parseInt(matcher.group(3));
                this.finishPosition = Integer.parseInt(matcher.group(4));
            } else {
                Log.error("Match not found for: '" + rawPositionDataString);
            }
        }
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getQuarterPosition() {
        return quarterPosition;
    }

    public int getHalfPosition() {
        return halfPosition;
    }

    public int getStrPosition() {
        return strPosition;
    }

    public int getThreeQuarterPosition() {
        return threeQuarterPosition;
    }

    public int getFinishPosition() {
        return finishPosition;
    }

    @Override
    public String toString() {
        String threeQuarter = "";
        if (hasThreeQuarter) {
            threeQuarter = " - " + threeQuarterPosition;
        }
        return "PositionData{" +
                startPosition +
                " - " + quarterPosition +
                " - " + halfPosition +
                " - " + strPosition +
                threeQuarter +
                " - " + finishPosition +
                '}';
    }
}