package com.alex;

/**
 * Use this class to parse NTFS's Alternate Data Stream in Windows directories
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADSReader {

    public static void display(Path toParse) {

        // We initialize some variables to hold our data.
        String path = toParse.toString();
        ArrayList<String> parsedADSSizeData = new ArrayList<>();
        ArrayList<String> parsedADS = new ArrayList<>();

        // This is the command we pass to the Windows console, which is "dir /r" in the specified Path.
        final String command = "cmd.exe /c dir " + path + " /r";

        // Here, we make a pattern for the matcher to use when isolating the ADS data.
        final Pattern pattern = Pattern.compile(
                "\\s*"                           // any amount of whitespace
                        + "([0123456789,])+\\s*" //  group 1 = digits (with possible comma), whitespace,
                        + "([^:]+:"              //  file name, then colon
                        + "[^:]+:"              //  then ADS, then colon,
                        + ".+)");               //  then everything else.

        try {

            // Run the command.
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;

                // Here, we try to match the each line with our pattern.
                while ((line = br.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);

                    // If we do find something that matches, we add it to our ArrayList.
                    if (matcher.matches()) {
                        parsedADSSizeData.add((matcher.group(1)));
                        parsedADS.add((matcher.group(2)));

                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Finally, we output our ADS data with appropriate formatting to look good with the other data.
        for (int z = 0; z<parsedADS.size(); z++)
            System.out.println(String.format("%1$" + 46 + "s", parsedADSSizeData.get(z)) + " " + parsedADS.get(z));

    }
}