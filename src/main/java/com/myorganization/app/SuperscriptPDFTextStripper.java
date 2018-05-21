package com.myorganization.app;

import java.io.*;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;


public class SuperscriptPDFTextStripper extends PDFTextStripper {
    private static final Logger Log = LogManager.getLogger("SuperscriptPDFTextStripper");

    public SuperscriptPDFTextStripper() throws IOException {
    }

    /**
     * Used for testing custom superscript detector
     *
     * @param args The pdf file to test
     * @throws IOException If there is an error parsing the document.
    */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = PDDocument.load(new File(args[0]))) {
                PDFTextStripper stripper = new SuperscriptPDFTextStripper();
                stripper.setSortByPosition(true);
                stripper.setStartPage(0);
                stripper.setEndPage(document.getNumberOfPages());

                Writer dummy = new BufferedWriter(new FileWriter("output.log"));
                stripper.writeText(document, dummy);

            }
        }
    }

    /**
     * Override the default functionality of PDFTextStripper to prepend a tilde to any superscripts
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (textPositions.size() > 0) {
            TextPosition firstChar = textPositions.get(0);

            // Assume first character is not superscripted
            StringBuilder modifiedString = new StringBuilder(string);

            for (int i = 1; i < string.length(); i++) {
                TextPosition text = textPositions.get(i);
                if ((firstChar.getY() > text.getY() && firstChar.getFontSize() > text.getFontSize())) {
                    modifiedString.insert(i, "~"); // Superscript delimeter is tilde "~"
                    break;
                }
            }

            super.writeString(modifiedString.toString(), textPositions);
        } else {
            Log.warn("Found empty textPositions passed to custom writeString!");
        }
    }

    /**
     * This will print the usage for this document.
     */

    private static void usage() {
        System.err.println("Usage: java " + SuperscriptPDFTextStripper.class.getName() + " <input-pdf>");
    }
}