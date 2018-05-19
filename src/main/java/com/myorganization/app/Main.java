package com.myorganization.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import static java.util.Arrays.asList;

/**
 * The defined parsing process of Equibase PDFs
 */
public final class Main {
    private static final Logger Log = LogManager.getLogger("Main");

    private static final String dataRegex = "(?:(?:(\\d{1,2}\\w{3}\\d{2}) \\d+(\\w{2,3})\\d+)|([-]+)) (\\d{1,2}[A]?) ([a-zA-Z'. ]+)\\(([a-zA-Z, .]+)\\) (\\d{2,3})\\S?\\s+((?:[L]{1}[ ]?[bf]{1,2})|(?:[L]{1})) (\\d{1,2}) (\\d{1,2}) ([-0-9\\/A-Za-z ]*) (\\d*\\.?\\d*)\\*? (.+)";
    private static final Pattern pattern = new Pattern(dataRegex, Pattern.MULTILINE);

    private static final String fractFinalRegex = ": (\\d*\\.?\\d*) (\\d*\\.?\\d*) ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) [a-zA-Z :]*((?:[0-9]+:)?[0-9]{2}.[0-9]{2})";
    private static final Pattern fractFinalPattern = new Pattern(fractFinalRegex, Pattern.MULTILINE);

    private static final String weatherRegex = ": ([a-zA-Z]+) \\w+: (.+)";
    private static final Pattern weatherPattern = new Pattern(weatherRegex, Pattern.MULTILINE);

    private static final String runUpRegex = ": (\\d+)";
    private static final Pattern runUpPattern = new Pattern(runUpRegex, Pattern.MULTILINE);

    private static final String trackRecordRegex = "([a-zA-Z ]*) Track Record: \\(([a-zA-Z \\(\\)]+) - ((?:[0-9]+:)?[0-9]{2}.[0-9]{2}) - (.+)\\)";
    private static final Pattern trackRecordPattern = new Pattern(trackRecordRegex, Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {

        try (PDDocument document = PDDocument.load(new File("old_test_dcrypt.pdf"))) { // old_test_dcrypt.pdf
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

            LocalTime fractTime1 = null;
            LocalTime fractTime2 = null;
            LocalTime fractTime3 = null;
            LocalTime finalTime = null;

            TrackWeather weatherCondition = null;
            TrackTypes trackCondition = null;
            TrackRecord trackRecord = null;

            int runUp = -1;

            int startPosition = -1;

            for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) { // Parse the PDF line by line
                String line = lines[lineIndex];
                if (i == 0) {
                    String[] data = line.split(" - ");
                    trackCode = RaceTracks.getTrackCode(data[0]);
                    raceDate = DateTimeUtil.parseDateString(data[1]);
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

                } else if ( (trackCode != null && StringUtils.containsIgnoreCase(line, RaceTracks.getTrack(trackCode))) || lineIndex == lines.length - 1 ) { // Determine new page from Track Name
                    // Finished processing page, create and save RaceEntry from data

                    if (fractTime1 == null || fractTime2 == null || fractTime3 == null) {
                        Log.error("Failed to parse fract times, exiting...");
                        return;
                    }
                    // TODO: Use track length to determine how many fractional times there should be: https://www.equibase.com/newfan/fractional_times.cfm
                    List<LocalTime> fractTimes = asList(fractTime1, fractTime2, fractTime3);

                    RaceInfo raceInfo = new RaceInfo(weatherCondition, trackCondition, trackRecord, fractTimes, finalTime, null, runUp, "", "", 0f);
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
                            String horseName = dataMatcher.group(5);
                            String jockeyName = dataMatcher.group(6);
                            int weight = Integer.parseInt(dataMatcher.group(7));
                            String me = dataMatcher.group(8);
                            int pp = Integer.parseInt(dataMatcher.group(9));
                            startPosition = Integer.parseInt(dataMatcher.group(10));
                            String rawPositionDataString = dataMatcher.group(11);
                            double odds = Double.parseDouble(dataMatcher.group(12));
                            String comments = dataMatcher.group(13);

                            Log.debug("Line: " + dataObject.getLine());
                            PositionData positionData = new PositionData(startPosition, rawPositionDataString, dataObject.isThreeQuarter());

                            RaceEntry newRaceEntry = new RaceEntry(trackCode, raceDate, pgm, jockeyName, raceNum, raceInfo, weight, me, pp, positionData, odds, comments);
                            Log.debug(newRaceEntry);

                            if (horseHashMap.containsKey(horseName)) { // If we already created this Horse

                            } else { // Otherwise, create a new Horse and store in horseHashMap
//                                Horse newHorse = new Horse();
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
                } else if (fetchStats && StringUtils.containsIgnoreCase(line, "Fractional Times")) { // Acts as marker to end of stat data, TODO: Is this always the case?
                    fetchStats = false; // First reset data fetching because table is finished

                    Matcher fractFinalMatcher = fractFinalPattern.matcher(line);
                    if (fractFinalMatcher.find()) { // If match
                        String fractTime1String = fractFinalMatcher.group(1);
                        String fractTime2String = fractFinalMatcher.group(2);
                        String fractTime3String = fractFinalMatcher.group(3);
                        String finalTimeString = fractFinalMatcher.group(4);

                        fractTime1 = DateTimeUtil.parseStopWatchString(fractTime1String);
                        fractTime2 = DateTimeUtil.parseStopWatchString(fractTime2String);
                        fractTime3 = DateTimeUtil.parseStopWatchString(fractTime3String);
                        finalTime = DateTimeUtil.parseStopWatchString(finalTimeString);
                    } else {
                        Log.error("Failed to match fractional time for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "weather")) {
                    Matcher weatherMatcher = weatherPattern.matcher(line);
                    if (weatherMatcher.find()) {
                        weatherCondition = TrackWeather.get(weatherMatcher.group(1));
                        trackCondition = TrackTypes.get(weatherMatcher.group(2));
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Run-Up")) {
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
                } else if (StringUtils.containsIgnoreCase(line, "Track Record")) {
                    Matcher trackRecordMatcher = trackRecordPattern.matcher(line);
                    if (trackRecordMatcher.find()) {
                        String trackLength = trackRecordMatcher.group(1);
                        Horse recordHorse = new Horse(trackRecordMatcher.group(2)); // TODO: How are we internally storing horses? Maybe update later if info available
                        LocalTime recordTime = DateTimeUtil.parseStopWatchString(trackRecordMatcher.group(3));
                        LocalDate recordDate = DateTimeUtil.parseDateString(trackRecordMatcher.group(4));

                        trackRecord = new TrackRecord(recordHorse, recordTime, recordDate);

                    } else {
                        Log.error("Failed to match Track Record/Length for line: " + line);
                    }
                } else {
                    // Really spammy log
//                    Log.debug("Ignoring line: " + line);
                }
                i++;
            }
        } catch (IOException e) {
            Log.error("Failed to load PDF document: " + e);
        } catch (Exception e) {
            Log.error("Failed to parse Document: " + e);
            e.printStackTrace();
        }

        Log.debug("Finished Successfully?");
    }
}