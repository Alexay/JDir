package jdir;

/**
 *  Use this class to get a header for the Standard Display output mode.
 *
 *  USED BY: StandardDisplay.java
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import static jdir.Main.linePrintSetting;

public class HeaderDataReader {

    public static void read(Path toParse) {

        // We initialize some variables to hold our data.
        String path = toParse.toString();
        ArrayList<String> output = new ArrayList<>();

        // This is the command we pass to the Windows console, which is "dir" in the specified Path.
        final String command = "cmd.exe /c dir " + path;

        try {
            // Run the command.
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                for (int i = 0; i<2; i++)
                output.add(br.readLine());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Finally, we output our Header data.
        for (int z = 0; z < output.size(); z++)
            P.printIt(output.get(z), linePrintSetting);
        System.out.println();

    }
}