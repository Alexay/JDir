package jdir;

import java.io.IOException;

/**
 *   An output mode that allows for any of the display modes to be printed page-by-page using the Windows "more"
 *   command with the /E option to allow for the user to press <SPACE> key for page scrolling.
 */
public class P {

    public static void display(String[] args) throws IOException, InterruptedException {
        String out = "";
        for (int i = 0; i<args.length; i++) {
            out += args[i]  + " ";
        }

        Process process =
                Runtime.getRuntime().exec("cmd /c java Main " + out + " | more /E");
        process.waitFor();
        byte[] data = new byte[65536];
        int size = process.getInputStream().read(data);

        System.out.println(data);
    }
}
