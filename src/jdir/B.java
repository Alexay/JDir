package jdir;

import joptsimple.OptionSet;

import java.nio.file.Path;
import static jdir.Main.linePrintSetting;

/**
 *  This is the "Bare" display mode, which outputs only the names of the files.
 *
 *  USED BY: Main.java
 */
public class B {

    public static void display(Path[] filesForDisplay, OptionSet options) {
        for (Path aPath : filesForDisplay) {

            String fileName;

            // If the user specifies the recursive option, we print the entire filepath.
            if (options.has("s"))
                fileName = aPath.toAbsolutePath().toString();
            else
                fileName = aPath.toString();

            if (options.has("l"))
                fileName = fileName.toLowerCase();


            P.printIt(fileName, linePrintSetting);
        }
    }
}