package com.myorganization.app;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.myorganization.app.models.RaceTracks;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;


/**
 * Creates a "Hello World" PDF using the built-in Helvetica font.
 * <p>
 * The example is taken from the PDF file format specification.
 */
public final class Main {

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
            for (String line : lines) {
                if (i == 0) {
                    String[] data = line.split(" - ");
                    trackCode = RaceTracks.getTrackCode(data[0]);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                    raceDate = LocalDate.parse(data[1], formatter);
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);

//                    System.out.println(line);
//                    System.out.println(raceNum);
                } else if (trackCode != null && StringUtils.containsIgnoreCase(line, RaceTracks.getTrack(trackCode))) { // Determine new page from Track Name
                    page++; // Increment the page, internal tracking of current page incremented each time we see "track name - date - race number"
                    String[] data = line.split(" - ");
                    raceNum = Integer.parseInt(data[2].split(" ")[1]);
//                    System.out.println(line);
//                    System.out.println(raceNum);


                } else if (stringSimilarity.apply(line, "Last Raced Pgm Horse Name (Jockey) Wgt M/E PP Start 1/4 1/2 3/4 Str Fin Odds Comments") < 5) { // Margin for parsing error is 5 character difference than defined string
                    fetchStats = true;
                } else if (fetchStats) { // The first data table header has been reached, so we start getting RaceEntries
                    // How do we parse this monster
                    String[] data = line.split(" ");
                } else if (line.contains("Fractional Times")) { // Acts as marker to end of stat data, TODO: Is this always the case?
                    fetchStats = false; // First reset data fetching because table is finished
                }
                System.out.println(line);
                i++;
            }
        }
    }
}