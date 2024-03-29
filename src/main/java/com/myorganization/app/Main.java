package com.myorganization.app;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    private static Logger Log;

    private static final String dataRegex = "(?:(?:(\\d{1,2}\\w{3}\\d{2}) [0-9~]{0,3}(\\w{2,3})[0-9~]{0,3})|([-]+)) (\\d{1,3}[A-Za-z]{0,3}|[-]+) ([-a-zA-Z'.\\(\\) ]+)\\(([-a-zA-Z,' .]+)\\) (\\d{2,3})[~]?\\S? ([LBb]?[a-zA-Z ]{1,5}|[- ]+) (\\d{1,2}|[-]) (\\d{1,2}|[-]+) ([-0-9\\/HeadNckos~ ]*) (?:(\\d*\\.?\\d*)\\*?)?(.+)";
    private static final Pattern pattern = new Pattern(dataRegex, Pattern.MULTILINE);

    private static final String weatherRegex = ": ?([a-zA-Z]+|) \\w+: ?(.+|)";
    private static final Pattern weatherPattern = new Pattern(weatherRegex, Pattern.MULTILINE);

    private static final String runUpRegex = ": (\\d+)";
    private static final Pattern runUpPattern = new Pattern(runUpRegex, Pattern.MULTILINE);

    private static final String trackRecordRegex = "Track Record: \\(([a-zA-Z'. \\(\\)]+) - ?(?:((?:[0-9]+:)?[0-9]{2}.[0-9]{2,3})|) - (.+)\\)";
    private static final Pattern trackRecordPattern = new Pattern(trackRecordRegex, Pattern.MULTILINE);

    private static final String colonRegex = ": (.+)";
    private static final Pattern colonPattern = new Pattern(colonRegex, Pattern.MULTILINE);

    private static final String splitTimeRegex = "\\(([0-9.:]{4,8})\\)";
    private static final Pattern splitTimePattern = new Pattern(splitTimeRegex, Pattern.MULTILINE);

    private static final int PDF_MINIMUM_SIZE_BYTES = 4000;

    public static void main(String[] args) throws IOException {
        // Suppress PDFBox No Unicode warnings
        java.util.logging.Logger.getLogger("org.apache.pdfbox")
                .setLevel(java.util.logging.Level.SEVERE);

        Log = LogManager.getLogger("Main");

        HashMap<String, Horse> horseHashMap = new HashMap<>();

        List<File> filesInFolder = Files.walk(Paths.get(args[0]))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        int fileParseCount = 0;
        for (File file : filesInFolder) {
            if (file.getAbsolutePath().contains("1991") || file.getAbsolutePath().contains("1992") ||
                    file.getAbsolutePath().contains("1993") || file.getAbsolutePath().contains("1994") ||
                    file.getAbsolutePath().contains("1995") || file.getAbsolutePath().contains("1996") ||
                    file.getAbsolutePath().contains("1997") || file.getAbsolutePath().contains("1998") ||
                    file.getAbsolutePath().contains("1999") || file.getAbsolutePath().contains("2000") ||
                    file.getAbsolutePath().contains("2001") || file.getAbsolutePath().contains("2002") ||
                    file.getAbsolutePath().contains("2003") || file.getAbsolutePath().contains("2004") ||
                    file.getAbsolutePath().contains("2005") || file.getAbsolutePath().contains("2006") ||
                    file.getAbsolutePath().contains("2007") || file.getAbsolutePath().contains("2008") ) {
                continue;
            }
            if (file.length() >= PDF_MINIMUM_SIZE_BYTES && file.getName().contains("decrypted")) { // Make sure file is not corrupt and decrypted
                if (fileParseCount == 0) {
                    Log.info("Starting parser!");
                } else if (fileParseCount % 1000 == 0) {
                    Log.info("Parsed " + fileParseCount + " pdfs");
                }
                parse(file, horseHashMap);
                fileParseCount++;
            }
        }

        Log.info(horseHashMap);

        Log.info("Finished parsing " + fileParseCount + " pdfs!");
    }

    public static void writeHashMapToFile(String filePath, HashMap<String, Horse> horseHashMap) {
        // Used for writing horses in HashMap to file
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            for (Horse horse : horseHashMap.values()) {
                printWriter.println(horse);
            }

            printWriter.close();
            Log.info("Done writing horseHash.txt");
        } catch (IOException e) {
            Log.error("Failed to write horseHash.txt: " + e.getMessage());
        }
    }

    public static void writeSerializedHashMap(String filePath, HashMap<String, Horse> horseHashMap) {
        // Serializing hashmap
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(horseHashMap);
            out.close();
            fileOut.close();
            Log.info("Serialized data is saved in " + filePath);
        } catch (IOException i) {
            Log.error("Failed to write serialized HashMap: " + i.getMessage());
        }
    }

    public static void parse(File file, HashMap<String, Horse> horseHashMap) {
        try (PDDocument document = PDDocument.load(file)) {
            // Create instance of our custom PDF Text Stripper to parse document
            PDFTextStripper tStripper = new SuperscriptPDFTextStripper();
            String pdfFileInText = tStripper.getText(document);

            // split by line
            String lines[] = pdfFileInText.split("\\r?\\n");
            int page = 0;
            int i = 0;

            // TODO: ENSURE ALL VARIABLES ARE SET TO NULL IF NOT FOUND, OR ELSE THE REUSE OF VARIABLES WILL GIVE INCORRECT DATA (the previous data point)

            String trackCode = null;
            LocalDate raceDate = null;
            int raceNum = 0;

            LevenshteinDistance stringSimilarity = new LevenshteinDistance();
            boolean fetchStats = false;
            boolean hasThreeQuarter = false;

            ArrayList<DebugDataObject> dataMatcherList = new ArrayList<>();
            int dataIndex = 0;

            LocalTime finalTime = null;

            ArrayList<LocalTime> fractTimes = new ArrayList<>();
            ArrayList<LocalTime> splitTimes = new ArrayList<>();

            TrackWeather weatherCondition = null;
            TrackTypes trackCondition = null;
            TrackRecord trackRecord = null;

            int runUp = -1;
            String winningBreeder = "";
            String winningOwner = "";

            // Init currency parser
            NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);
            ((DecimalFormat) numFormat).setParseBigDecimal(true);

            BigDecimal totalPool = BigDecimal.ZERO;

            for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) { // Parse the PDF line by line
                String line = lines[lineIndex];

                // Handle special case for excluded Fractional Times section in Fractional/Final line after table
                if (fetchStats && StringUtils.containsIgnoreCase(line, "Final Time:") && !StringUtils.containsIgnoreCase(line, "Fractional")) {
                    fetchStats = false; // Make sure to turn of table data row fetching

                    if (line.length() <= "Final Time: ".length()) { // Make sure Final Time isn't empty
                        finalTime = null;
                    } else {
                        if (line.contains("N/A")) {
                            finalTime = null;
                        } else { // Hopefully some real fucking digits
                            Matcher finalMatcher = colonPattern.matcher(line);
                            if (finalMatcher.find()) {
                                finalTime = DateTimeUtil.parseStopWatchString(finalMatcher.group(1));
                            } else {
                                Log.error("Failed to handle special case of no Fractional Time for line: " + line);
                            }
                        }
                    }
                }

                if (i == 0) {
                    String[] data = line.split(" - ");
                    trackCode = RaceTracks.getTrackCode(data[0]);
                    raceDate = DateTimeUtil.parseDateString(data[1]);
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

                } else if ((trackCode != null && StringUtils.startsWithIgnoreCase(line.trim(), RaceTracks.getTrack(trackCode)) && StringUtils.countMatches(line, " - ") == 2) || lineIndex == lines.length - 1) { // Determine new page from Track Name
                    // Finished processing page, create and save RaceEntry from data

                    RaceInfo raceInfo = new RaceInfo(weatherCondition, trackCondition, trackRecord, fractTimes, finalTime, splitTimes, runUp, winningBreeder, winningOwner, totalPool);
                    Log.debug(raceInfo);
                    Log.debug(trackRecord);

                    for (; dataIndex < dataMatcherList.size(); dataIndex++) {
                        DebugDataObject dataObject = dataMatcherList.get(dataIndex);
                        Matcher dataMatcher = dataObject.getDataMatcher();
                        if (dataMatcher != null) {
                            // TODO: Do we even need last race location and date? or if they never raced before?
                            String lastHorseRaceDateString = dataMatcher.group(1);
                            String lastHorseRaceTrackString = dataMatcher.group(2);
                            String neverRacedFlag = dataMatcher.group(3);

                            String pgm = null;
                            if (!dataMatcher.group(4).contains("-")) {
                                pgm = dataMatcher.group(4);
                            }
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
                            String oddsString = dataMatcher.group(12);
                            double odds = -1;
                            String comments = dataMatcher.group(13).trim();
                            if (oddsString == null || oddsString.length() == 0) { // TODO: Handle declared odds case
                                String failedOdds = comments; // TODO: Remove legacy variable
                                Log.warn("Detected non-existent odds but instead: " + failedOdds + " on line: " + dataObject.getLine());
                                if (failedOdds.contains("--")) { // TODO: Does this still occur with new regex?
                                    Log.warn("Detected empty Finishing Position, attempting to fix for line: " + dataObject.getLine());
                                    rawPositionDataString += " 0";
                                }
                                comments = failedOdds; // Make whatever is left after non-existent odds the comments for this entry
                            } else {
                                try {
                                    odds = Double.parseDouble(oddsString);
                                } catch (NumberFormatException e) {
                                    Log.error("Failed to parse declared odds on line: " + dataObject.getLine());
                                } catch (NullPointerException e) {
                                    Log.error("Failed to capture correct odds: " + dataObject.getLine());
                                }
                            }

                            PositionData positionData = new PositionData(startPosition, rawPositionDataString, dataObject.isThreeQuarter());

                            RaceEntry newRaceEntry = new RaceEntry(trackCode, raceDate, pgm, jockeyName, raceNum, raceInfo, weight, me, pp, positionData, odds, comments);

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
                        raceNum = Integer.parseInt(data[2].split(" ")[1]); // TODO: Rework, not matching beginning of page anymore
                    }
                } else if (StringUtils.startsWithIgnoreCase(line, "Last Raced Pgm Horse Name (Jockey) Wgt")) { // Margin for parsing error is 5 character difference than defined string
                    // TODO: Need to create system to dynamically match the header labels to position data, ex. https://i.imgur.com/Qcx5Fcc.png vs normie header
                    // TODO: Ignore all really short races??? https://i.imgur.com/ebQYViw.png
                    fetchStats = true;

                    if (!line.contains(" Odds")) Log.error("FAILED ODDS CHECK: " + line);

                    hasThreeQuarter = line.contains("3/4");
                } else if (fetchStats && ( !line.contains("Fractional Times") || !line.contains("Final Time:") )) { // The first data table header has been reached, so we start getting RaceEntries
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
                    String sanitizedLine = line.replace("*", "0"); // Need to handle random missing * entries, will add 0 to odds inadvertently but shouldn't matter
                    Matcher dataMatcher = pattern.matcher(sanitizedLine);
                    if (dataMatcher.find()) {
                        DebugDataObject newPair = new DebugDataObject(line, dataMatcher, hasThreeQuarter);
                        dataMatcherList.add(newPair);
                    } else {
                        Log.error("Failed to match table data row: " + line);
                    }
                } else if (fetchStats && StringUtils.containsIgnoreCase(line, "Fractional Times:")) {
                    // Acts as marker to end of stat data, TODO: Is this always the case? so far yes except for special case handled above
                    fetchStats = false; // First reset data fetching because table is finished

                    fractTimes = new ArrayList<>();

                    String[] rawFractData = line.replace("Fractional Times:", "")
                            .replace("Final Time:", "")
                            .trim()
                            .split(" ");
                    int numOfFractionalTimes = rawFractData.length;
                    for (int j = 0; j < numOfFractionalTimes - 2; j++) {
                        if (rawFractData[j].length() > 0 && !rawFractData[j].contains("N/A")) {
                            LocalTime newFractTime = DateTimeUtil.parseStopWatchString(rawFractData[j]);
                            fractTimes.add(newFractTime);
                        } else {
                            fractTimes.add(null);
                        }
                    }

                    String finalTimeString = rawFractData[numOfFractionalTimes - 1];
                    if (finalTimeString.contains("N/A")) {
                        finalTime = null;
                    } else {
                        finalTime = DateTimeUtil.parseStopWatchString(finalTimeString);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Weather:") && StringUtils.containsIgnoreCase(line, "Track:")) {
                    Matcher weatherMatcher = weatherPattern.matcher(line);
                    if (weatherMatcher.find()) {
                        String weather = weatherMatcher.group(1);
                        String track = weatherMatcher.group(2);
                        if (weather.length() == 0) {
                            weatherCondition = null;
                        } else {
                            weatherCondition = TrackWeather.get(weather);
                        }
                        if (track.length() == 0) {
                            trackCondition = null;
                        } else {
                            trackCondition = TrackTypes.get(track);
                        }
                    } else {
                        Log.error("Failed to match weather for line: " + line);
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
                } else if (StringUtils.containsIgnoreCase(line, "Track Record: (")) {
                    Matcher trackRecordMatcher = trackRecordPattern.matcher(line);
                    if (trackRecordMatcher.find()) {
                        // If error here, then add missing
                        Horse recordHorse = new Horse(trackRecordMatcher.group(1));
                        String recordTimeString = trackRecordMatcher.group(2);
                        LocalTime recordTime = null;
                        if (recordTimeString != null) {
                            recordTime = DateTimeUtil.parseStopWatchString(recordTimeString);
                        }
                        LocalDate recordDate = DateTimeUtil.parseDateString(trackRecordMatcher.group(3));

                        trackRecord = new TrackRecord(recordHorse, recordTime, recordDate);
                    } else if (StringUtils.countMatches(line, "(") != 1 || StringUtils.countMatches(line, ")") != 1) { // Handle special case of super long Track Length - see Race 2 of 110295
                        String modifiedLine = line + " " + lines[lineIndex+1]; // Assume next line is the rest of the line

                        trackRecordMatcher = trackRecordPattern.matcher(modifiedLine);
                        if (trackRecordMatcher.find()) {
                            // If error here, then add missing
                            Horse recordHorse = new Horse(trackRecordMatcher.group(1));
                            LocalTime recordTime = DateTimeUtil.parseStopWatchString(trackRecordMatcher.group(2));
                            LocalDate recordDate = DateTimeUtil.parseDateString(trackRecordMatcher.group(3));

                            trackRecord = new TrackRecord(recordHorse, recordTime, recordDate);
                        } else {
                            Log.error("Failed to attempt to fix super long Track Record line:" + line);
                        }
                    } else {
                        Log.error("Failed to match Track Record/Length for line: " + line);
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Split Times:")) {
                    // Handling of special cases lead us back to regex instead of splitting by space
                    // ex. (1.00:27)(31:13) (30:56) (30:51)
                    // ex. (1.17:51)(25:28) (27:67) (26:60)
                    // ex. (51:90) (1.00:97)(54:22)
                    splitTimes = new ArrayList<>(); // Reset splitTimes
                    String splitTimeString = line.replace("-.", ""); // Strip "-." to handle special case (ex: "(24:39) (25:67) (-.33:88) (1.01:74)" )
                    Matcher splitTimeMatcher = splitTimePattern.matcher(splitTimeString);
                    while(splitTimeMatcher.find()) { // Loop through each separate match
                        try {
                            LocalTime newSplitTime = DateTimeUtil.parseShortStopWatchString(splitTimeMatcher.toString());
                            splitTimes.add(newSplitTime);
                        } catch (IllegalArgumentException e) {
                            Log.error("Failed to parse SplitTime on line: " + line);
                        }
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Breeder:")) {
                    if (line.length() <= "Breeder: ".length()) { // Handle empty breeder case
                        winningBreeder = null;
                    } else { // Breeder not empty, so try regex
                        Matcher winningBreederMatcher = colonPattern.matcher(line);
                        if (winningBreederMatcher.find()) {
                            winningBreeder = winningBreederMatcher.group(1).trim();
                        } else {
                            Log.error("Failed to match Winning Breeder for line: " + line);
                        }
                    }
                } else if (StringUtils.containsIgnoreCase(line, "Winning Owner:")) {
                    if (line.length() <= "Winning Owner: ".length()) { // Handle empty breeder case
                        winningOwner = null;
                    } else { // Owner not empty, so try regex
                        Matcher winningOwnerMatcher = colonPattern.matcher(line);
                        if (winningOwnerMatcher.find()) {
                            winningOwner = winningOwnerMatcher.group(1).trim();
                        } else {
                            Log.error("Failed to match Winning Owner for line: " + line);
                        }
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
        } catch (IOException e) {
            Log.error("Failed to load PDF document: " + file.getName());
        } catch (Exception e) {
            Log.error("Failed to parse Document: " + file.getName());
            e.printStackTrace();
        }
    }
}