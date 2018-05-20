package com.myorganization.app;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.myorganization.app.models.*;
import jregex.Matcher;
import jregex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * The defined parsing process of Equibase PDFs
 */
public final class Main {
    private static final Logger Log = LogManager.getLogger("Main");

    private static final String dataRegex = "(?:(?:(\\d{1,2}\\w{3}\\d{2}) \\d+(\\w{2,3})\\d+)|([-]+)) (\\d{1,2}[Aa]?) ([-a-zA-Z'.\\(\\) ]+)\\(([-a-zA-Z, .]+)\\) (\\d{2,3})\\S?\\s+((?:[L]?[ ]?[bfc]{1,2})|[L]|[-]) (\\d{1,2}|[-]) (\\d{1,2}) ([-0-9\\/A-Za-z ]*) (\\d*\\.?\\d*)\\*? (.+)";
    private static final Pattern pattern = new Pattern(dataRegex, Pattern.MULTILINE);

    private static final String oneFractFinalRegex = ": ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) Final Time: ((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern oneFractFinalPattern = new Pattern(oneFractFinalRegex, Pattern.MULTILINE);

    private static final String twoFractFinalRegex = ": ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) Final Time: ((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern twoFractFinalPattern = new Pattern(twoFractFinalRegex, Pattern.MULTILINE);

    private static final String threeFractFinalRegex = ": ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) Final Time: ((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern threeFractFinalPattern = new Pattern(threeFractFinalRegex, Pattern.MULTILINE);

    private static final String fourFractFinalRegex = ": ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) Final Time: ((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern fourFractFinalPattern = new Pattern(fourFractFinalRegex, Pattern.MULTILINE);

    private static final String fiveFractFinalRegex = ": ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) Final Time: ((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern fiveFractFinalPattern = new Pattern(fiveFractFinalRegex, Pattern.MULTILINE);

    private static final String weatherRegex = ": ([a-zA-Z]+) \\w+: (.+)";
    private static final Pattern weatherPattern = new Pattern(weatherRegex, Pattern.MULTILINE);

    private static final String runUpRegex = ": (\\d+)";
    private static final Pattern runUpPattern = new Pattern(runUpRegex, Pattern.MULTILINE);

    private static final String trackRecordRegex = "([a-zA-Z ]*) Track Record: \\(([a-zA-Z' \\(\\)]+) - ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) - (.+)\\)";
    private static final Pattern trackRecordPattern = new Pattern(trackRecordRegex, Pattern.MULTILINE);

    private static final String colonRegex = ": (.+)";
    private static final Pattern colonPattern = new Pattern(colonRegex, Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {

        try (PDDocument document = PDDocument.load(new File("turn-of-the-century.pdf"))) { // old_test_dcrypt.pdf
            PDFTextStripper tStripper = new PDFTextStripper();
            String pdfFileInText = tStripper.getText(document);

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

            ArrayList<DebugDataObject> dataMatcherList = new ArrayList<>();
            int dataIndex = 0;

            HashMap<String, Horse> horseHashMap = new HashMap<>();

            int numOfFractionalTimes = 0;
            LocalTime fractTime1 = null;
            LocalTime fractTime2 = null;
            LocalTime fractTime3 = null;
            LocalTime finalTime = null;

            ArrayList<LocalTime> fractTimes = new ArrayList<>();
            ArrayList<LocalTime> splitTimes = new ArrayList<>();

            TrackWeather weatherCondition = null;
            TrackTypes trackCondition = null;
            TrackRecord trackRecord = null;

            int runUp = -1;
            String winningBreeder = "";
            String winningOwner = "";
            String trackLength = "";

            // Init currency parser
            NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);
            ((DecimalFormat)numFormat).setParseBigDecimal(true);

            BigDecimal totalPool = BigDecimal.ZERO;

            for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) { // Parse the PDF line by line
                String line = lines[lineIndex];
                if (i == 0) {
                    String[] data = line.split(" - ");
                    trackCode = RaceTracks.getTrackCode(data[0]);
                    raceDate = DateTimeUtil.parseDateString(data[1]);
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

                } else if ( (trackCode != null && StringUtils.containsIgnoreCase(line, RaceTracks.getTrack(trackCode))) || lineIndex == lines.length - 1 ) { // Determine new page from Track Name
                    // Finished processing page, create and save RaceEntry from data

                    RaceInfo raceInfo = new RaceInfo(weatherCondition, trackCondition, trackRecord, fractTimes, finalTime, splitTimes, runUp, winningBreeder, winningOwner, totalPool);
                    Log.debug(raceInfo);
                    Log.debug(trackRecord);

                    for (; dataIndex < dataMatcherList.size(); dataIndex++ ) {
                        DebugDataObject dataObject = dataMatcherList.get(dataIndex);
                        Matcher dataMatcher = dataObject.getDataMatcher();
                        if (dataMatcher != null) {
                            // TODO: Do we even need last race location and date? or if they never raced before?
                            String lastHorseRaceDateString = dataMatcher.group(1);
                            String lastHorseRaceTrackString = dataMatcher.group(2);
                            String neverRacedFlag = dataMatcher.group(3);

                            String pgm = dataMatcher.group(4);
                            String horseName = dataMatcher.group(5).trim();
                            String jockeyName = dataMatcher.group(6);
                            int weight = Integer.parseInt(dataMatcher.group(7));
                            String me = dataMatcher.group(8);
                            int pp = 0;
                            if (!dataMatcher.group(9).contains("-")) {
                                pp = Integer.parseInt(dataMatcher.group(9));
                            }
                            int startPosition = 0;
                            if (!dataMatcher.group(10).contains("-")) {
                                startPosition = Integer.parseInt(dataMatcher.group(10));
                            }
                            String rawPositionDataString = dataMatcher.group(11);
                            double odds = Double.parseDouble(dataMatcher.group(12));
                            String comments = dataMatcher.group(13);

                            Log.debug("Line: " + dataObject.getLine());
                            PositionData positionData = new PositionData(startPosition, rawPositionDataString, dataObject.isThreeQuarter());

                            RaceEntry newRaceEntry = new RaceEntry(trackCode, raceDate, pgm, jockeyName, raceNum, raceInfo, weight, me, pp, positionData, odds, comments);
                            Log.debug(newRaceEntry);

                            if (horseHashMap.containsKey(horseName)) { // If we already created this Horse
                                horseHashMap.get(horseName).addRaceEntry(newRaceEntry);
                            } else { // Otherwise, create a new Horse and store in horseHashMap
                                // Create new Horse, add this entry to record, and then store horse for future
                                Horse newHorse = new Horse(horseName);
                                newHorse.addRaceEntry(newRaceEntry);
                                horseHashMap.put(horseName, newHorse);
                            }
                        } else {
                            Log.error("Error: Null dataMatcher for horse data row");
                        }
                    }

                    page++; // Increment the page, internal tracking of current page incremented each time we see "track name - date - race number"
                    if (lineIndex != lines.length - 1) {
                        String[] data = line.split(" - ");
                        raceNum = Integer.parseInt(data[2].split(" ")[1]);
                    }
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
                    Matcher dataMatcher = pattern.matcher(line);
                    if (dataMatcher.find()) {
                        DebugDataObject newPair = new DebugDataObject(line, dataMatcher, hasThreeQuarter);
                        dataMatcherList.add(newPair);
                    } else {
                        Log.error("Failed to match table data row: " + line);
                    }
                } else if (fetchStats && StringUtils.containsIgnoreCase(line, "Fractional Times:")) { // TODO: Redo fractional time parsing, does not account for 4th or 5th fractional column
                    // Acts as marker to end of stat data, TODO: Is this always the case?
                    fetchStats = false; // First reset data fetching because table is finished

                    fractTimes = new ArrayList<>();

                    Matcher fractFinalMatcher = null;
                    if (numOfFractionalTimes == 0) {
                        Log.error("Failed to get numOfFractionalTimes for: " + trackLength);
                    } else if (numOfFractionalTimes == 1) {
                        fractFinalMatcher = twoFractFinalPattern.matcher(line);
                    } else if (numOfFractionalTimes == 2) {
                        fractFinalMatcher = twoFractFinalPattern.matcher(line);
                    } else if (numOfFractionalTimes == 3) {
                        fractFinalMatcher = threeFractFinalPattern.matcher(line);
                    } else if (numOfFractionalTimes == 4) {
                        fractFinalMatcher = fourFractFinalPattern.matcher(line);
                    } else if (numOfFractionalTimes == 5) {
                        fractFinalMatcher = fiveFractFinalPattern.matcher(line);
                    }

                    if (fractFinalMatcher != null && fractFinalMatcher.find()) { // If match
                        for (int j = 1; j <= numOfFractionalTimes; j++) {
                            LocalTime fractTime = DateTimeUtil.parseStopWatchString(fractFinalMatcher.group(j));
                            fractTimes.add(fractTime);
                        }

                        String finalTimeString = fractFinalMatcher.group(numOfFractionalTimes+1);
                        finalTime = DateTimeUtil.parseStopWatchString(finalTimeString);
                    } else {
                        Log.error("Failed to match fractional time for line: " + line);
                        Log.error(trackLength);
                    }

                    numOfFractionalTimes = 0;
                } else if (StringUtils.containsIgnoreCase(line, "Weather:")) {
                    Matcher weatherMatcher = weatherPattern.matcher(line);
                    if (weatherMatcher.find()) {
                        // If error, add missing TrackWeather or TrackTypes
                        weatherCondition = TrackWeather.get(weatherMatcher.group(1));
                        trackCondition = TrackTypes.get(weatherMatcher.group(2));
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Run-Up:")) { // TODO: Add parsing for optional Temporary Rail field on the same line
                    Matcher runUpMatcher = runUpPattern.matcher(line);
                    if (runUpMatcher.find()) {
                        try {
                            runUp = Integer.parseInt(runUpMatcher.group(1));
                        } catch (NumberFormatException e) {
                            Log.error("Failed to parse Run-Up as Integer: " + runUp);
                        }
                    } else {
                        Log.error("Failed to match Run-Up for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Track Record:")) {
                    Matcher trackRecordMatcher = trackRecordPattern.matcher(line);
                    if (trackRecordMatcher.find()) {
                        trackLength = trackRecordMatcher.group(1).trim();
                        // If error here, then add missing TrackLength
                        numOfFractionalTimes = TrackLength.getNumOfFractionalTimes(trackLength);
                        Horse recordHorse = new Horse(trackRecordMatcher.group(2)); // TODO: How are we internally storing horses? Maybe update later if info available
                        LocalTime recordTime = DateTimeUtil.parseStopWatchString(trackRecordMatcher.group(3));
                        LocalDate recordDate = DateTimeUtil.parseDateString(trackRecordMatcher.group(4));

                        trackRecord = new TrackRecord(recordHorse, recordTime, recordDate);

                    } else {
                        Log.error("Failed to match Track Record/Length for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Split Times:")) {
                    splitTimes = new ArrayList<>(); // Reset splitTimes
                    String[] splitTimesData = line.split(" ");
                    for (int j = 2; j < splitTimesData.length ; j++) { // Loop through each split time, parse, and add to fresh splitTimes array
                        LocalTime newSplitTime = DateTimeUtil.parseShortStopWatchString(splitTimesData[j].trim());
                        splitTimes.add(newSplitTime);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Breeder:")) {
                    Matcher winningBreederMatcher = colonPattern.matcher(line);
                    if (winningBreederMatcher.find()) {
                        winningBreeder = winningBreederMatcher.group(1).trim();
                    } else {
                        Log.error("Failed to match Winning Breeder for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Winning Owner:")) {
                    Matcher winningOwnerMatcher = colonPattern.matcher(line);
                    if (winningOwnerMatcher.find()) {
                        winningOwner = winningOwnerMatcher.group(1).trim();
                    } else {
                        Log.error("Failed to match Winning Owner for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Total WPS Pool:")) {
                    Matcher poolMatcher = colonPattern.matcher(line);
                    if (poolMatcher.find()) {
                        try {
                            totalPool = (BigDecimal) numFormat.parse(poolMatcher.group(1));
                        } catch (ParseException e) {
                            Log.error("Failed to parse currency value: " + line + " with error: " + e);
                        }
                    } else {
                        Log.error("Failed to match Total WPS Pool for line: " + line);
                    }
                } else {
                        // Really spammy log
//                    Log.debug("Ignoring line: " + line);
                }
                i++;
            }

            horseHashMap.values().forEach(Log::info);
        } catch (IOException e) {
            Log.error("Failed to load PDF document: " + e);
        } catch (Exception e) {
            Log.error("Failed to parse Document: " + e);
            e.printStackTrace();
        }

        Log.info("Finished Successfully");
    }
}