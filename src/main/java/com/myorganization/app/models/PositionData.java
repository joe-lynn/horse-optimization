package com.myorganization.app.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PositionData {
    private int startPosition;
    private int quarterPosition;
    private int halfPosition;
    private int strPosition;
    private int threeQuarterPosition;
    private int finishPosition;

    private static final String fiveRegex = "(\\d)[1-9/a-zA-Z]* (\\d)[1-9/a-zA-Z]* (\\d)[1-9/a-zA-Z]* (\\d)[1-9/a-zA-Z]* (\\d)";
    private static final String fourRegex = "(\\d)[1-9/a-zA-Z]* (\\d)[1-9/a-zA-Z]* (\\d)[1-9/a-zA-Z]* (\\d)";
    private static final Pattern fivePattern = Pattern.compile(fiveRegex, Pattern.MULTILINE);
    private static final Pattern fourPattern = Pattern.compile(fourRegex, Pattern.MULTILINE);

    public PositionData(int startPosition, int quarterPosition, int halfPosition, int strPosition, int threeQuarterPosition, int finishPosition) {
        this.startPosition = startPosition;
        this.quarterPosition = quarterPosition;
        this.halfPosition = halfPosition;
        this.strPosition = strPosition;
        this.threeQuarterPosition = threeQuarterPosition;
        this.finishPosition = finishPosition;
    }

    public PositionData(String rawPositionDataString, boolean hasThreeQuarter) {
        // We are looking for 4 single digits
        // EX: 5 41 1/2 31/2 11/2 15 3/4
        // EX: 12 12 13 1/2 11 1/4
        // Remove bunch of common erroneous superscripts
        System.out.println("Pre: " + rawPositionDataString);
        rawPositionDataString = rawPositionDataString.replace("1/2", "");
        rawPositionDataString = rawPositionDataString.replace("Head", "");
        rawPositionDataString = rawPositionDataString.replace("1/4", "");
        rawPositionDataString = rawPositionDataString.replace("2/4", "");
        rawPositionDataString = rawPositionDataString.replace("3/4", "");
        rawPositionDataString = rawPositionDataString.replace("Neck", "");
        rawPositionDataString = rawPositionDataString.replace("Nose", "");
        rawPositionDataString = rawPositionDataString.replace("  ", " ");
        System.out.println("Post: " + rawPositionDataString);
        System.out.println(hasThreeQuarter);



        // Some have 3/4 column, and some do, hasThreeQuarter is flag to determine when it does and doesn't
        if (hasThreeQuarter) {
            Matcher matcher = fivePattern.matcher(rawPositionDataString);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println(matcher.group(i));
                }
            } else {
                System.out.println("Match not found for: " + rawPositionDataString);
            }
        } else {
            Matcher matcher = fourPattern.matcher(rawPositionDataString);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println(matcher.group(i));
                }
            } else {
                System.out.println("Match not found for: " + rawPositionDataString);
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
}