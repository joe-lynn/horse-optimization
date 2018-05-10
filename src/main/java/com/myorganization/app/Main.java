package com.myorganization.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myorganization.app.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static java.util.Arrays.asList;


/**
 * Creates a "Hello World" PDF using the built-in Helvetica font.
 * <p>
 * The example is taken from the PDF file format specification.
 */
public final class Main {

    private static final String dataRegex = "(?:(?:(\\d{1,2}\\w{3}\\d{2}) \\d+(\\w{2,3})\\d+)|([-]+)) (\\d) ((?:[a-zA-Z']+\\s){1,3})\\(([a-zA-Z, .]+)\\) (\\d{2,3})\\S?\\s+((?:[L]{1}[ ]?[bf]{1,2})|(?:[L]{1})) (\\d{1}) (\\d{1}) ([0-9\\/A-Za-z ]*) (\\d*\\.?\\d*)\\*? (.+)";
    private static final Pattern pattern = Pattern.compile(dataRegex, Pattern.MULTILINE);

    private static final String fractFinalRegex = ": (\\d*\\.?\\d*) (\\d*\\.?\\d*) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) [a-zA-Z :]*((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern fractFinalPattern = Pattern.compile(fractFinalRegex, Pattern.MULTILINE);

    private static final String weatherRegex = ": ([a-zA-Z]+) \\w+: (.+)";
    private static final Pattern weatherPattern = Pattern.compile(weatherRegex, Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {

        try (PDDocument document = PDDocument.load(new File("old_test_dcrypt.pdf"))) { // old_test_dcrypt.pdf

            PDFTextStripper tStripper = new PDFTextStripper();

            String pdfFileInText = tStripper.getText(document);
            //System.out.println("Text:" + st);

            // split by whitespace
            String lines[] = pdfFileInText.split("\\r?\\n");
            int page = 0;
            int i = 0;

            String trackCode = null;
            LocalDate raceDate = null;
            int raceNum = 0;

            LevenshteinDistance stringSimilarity = new LevenshteinDistance();
            boolean fetchStats = false;
            boolean hasThreeQuarter = false;

            ArrayList<Matcher> dataMatcherList = new ArrayList<>();

            LocalTime fractTime1 = null;
            LocalTime fractTime2 = null;
            LocalTime fractTime3 = null;
            LocalTime finalTime = null;

            TrackWeather weatherCondition = null;
            TrackTypes trackCondition = null;

            int startPosition = -1;
            int quarterPosition = -1;
            int halfPosition = -1;
            int strPosition = -1;
            int threeQuarterPosition = -1;
            int finishPosition = -1;

            for (String line : lines) {
                if (i == 0) {
                    String[] data = line.split(" - ");
                    trackCode = RaceTracks.getTrackCode(data[0]);
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM d, yyyy");
                    raceDate = LocalDate.parse(data[1], formatter);
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

//                    System.out.println(line);
//                    System.out.println(raceNum);
                } else if (trackCode != null && StringUtils.containsIgnoreCase(line, RaceTracks.getTrack(trackCode))) { // Determine new page from Track Name
                    // Finished processing page, create and save RaceEntry from data
                    for (Matcher dataMatcher : dataMatcherList) {
                        if (dataMatcher != null && dataMatcher.find()) {
                            String lastHorseRaceDateString = dataMatcher.group(1);
                            String lastHorseRaceTrackString = dataMatcher.group(2);
                            String neverRacedFlag = dataMatcher.group(3);
                            int pgm = Integer.parseInt(dataMatcher.group(4));
                            String horseName = dataMatcher.group(5);
                            String jockeyName = dataMatcher.group(6);
                            int weight = Integer.parseInt(dataMatcher.group(7));
                            String me = dataMatcher.group(8);
                            int pp = Integer.parseInt(dataMatcher.group(9));
                            startPosition = Integer.parseInt(dataMatcher.group(10));
                            String rawPositionDataString = dataMatcher.group(11);
                            double odds = Double.parseDouble(dataMatcher.group(12));
                            String comments = dataMatcher.group(13);

                            PositionData positionData = new PositionData(rawPositionDataString, hasThreeQuarter);
                            List<LocalTime> fractTimes = asList(fractTime1, fractTime2, fractTime3);
//                        RaceInfo newRaceInfo = new RaceInfo();

//                        RaceEntry newRaceEntry = new RaceEntry(trackCode, raceDate, raceNum, raceInfo, weight, pp, positionData, odds, comments);
                        }
                    }


                    page++; // Increment the page, internal tracking of current page incremented each time we see "track name - date - race number"
                    String[] data = line.split(" - ");
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

                } else if (stringSimilarity.apply(line, "Last Raced Pgm Horse Name (Jockey) Wgt M/E PP Start 1/4 1/2 3/4 Str Fin Odds Comments") < 5) { // Margin for parsing error is 5 character difference than defined string
                    fetchStats = true;
                    hasThreeQuarter = line.contains("3/4");
                } else if (fetchStats && !line.contains("Fractional Times")) { // The first data table header has been reached, so we start getting RaceEntries
                    /** Parsing Guide
                     *  Group # - Contents - Example
                     *  0 - Full String of match - "2Feb14 6AQU3 6 Star Empress (Rice, Taylor) 116» L b 5 4 5 41 1/2 31/2 11/2 15 3/4 0.45* ins 1/2-5/16, 4w upper"
                     *  1 - Horse's Last Race Date - "2Feb14"
                     *  2 - Horse's Last Race Track - "AQU"
                     *  3 - "---" if Horse never raced before -
                     *  4 - Pgm - 6
                     *  5 - Horse Name - Star Empress
                     *  6 - Jockey Name (Last Name, First Name) - Rice, Taylor
                     *  7 - Horse Weight (pounds) - 116
                     *  8 - M/E - "L b"
                     *  9 - PP - 5
                     *  10 - Start Position - 4
                     *  11 - Raw data of remaining horse positions to be parsed - "5 41 1/2 31/2 11/2 15 3/4"
                     *  12 - The Odds for this Horse in this Race - 0.45
                     *  13 - Comments on race - "ins 1/2-5/16, 4w upper"
                     */
                    dataMatcherList.add(pattern.matcher(line));
                } else if (fetchStats && StringUtils.containsIgnoreCase(line, "Fractional Times")) { // Acts as marker to end of stat data, TODO: Is this always the case?
                    fetchStats = false; // First reset data fetching because table is finished

                    Matcher fractFinalMatcher = fractFinalPattern.matcher(line);
                    if (fractFinalMatcher.find()) { // If match
                        String fractTime1String = fractFinalMatcher.group(1);
                        String fractTime2String = fractFinalMatcher.group(2);
                        String fractTime3String = fractFinalMatcher.group(3);
                        String finalTimeString = fractFinalMatcher.group(4);

                        DateTimeFormatter threePartFormatter = DateTimeFormat.forPattern("mm:ss.SS");
                        DateTimeFormatter twoPartFormatter = DateTimeFormat.forPattern("ss.SS");

                        fractTime1 = LocalTime.parse(fractTime1String, twoPartFormatter); // Assume two part, or else these horses slow as fuck

                        if (fractTime2String.length() > 5) {
                            fractTime2 = LocalTime.parse(fractTime2String, threePartFormatter);
                        } else {
                            fractTime2 = LocalTime.parse(fractTime2String, twoPartFormatter);
                        }

                        if (fractTime3String.length() > 5) {
                            fractTime3 = LocalTime.parse(fractTime3String, threePartFormatter);
                        } else {
                            fractTime3 = LocalTime.parse(fractTime3String, twoPartFormatter);
                        }

                        if (finalTimeString.length() > 5) {
                            finalTime = LocalTime.parse(finalTimeString, threePartFormatter);
                        } else {
                            finalTime = LocalTime.parse(finalTimeString, twoPartFormatter);
                        }
                    } else {
                        System.out.println(line);
                        System.out.println("Failed to match for line");
                    }
                } else if (StringUtils.containsIgnoreCase(line, "weather")) {
                    Matcher weatherMatcher = weatherPattern.matcher(line);
                    if (weatherMatcher.find()) {
                        weatherCondition = TrackWeather.get(weatherMatcher.group(1));
                        trackCondition = TrackTypes.get(weatherMatcher.group(2));
                    }
                }
//                System.out.println(line);


                i++;
            }
        }
    }
}