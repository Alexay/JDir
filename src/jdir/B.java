package jdir;

import joptsimple.OptionSet;

import java.nio.file.Path;

/**
 *  This is the "Bare" display mode, which outputs only the names of the files.
 *
 *  USED BY: Main.java
 */
public class B {

    public static void display(Path[] filesForDisplay, OptionSet options) {
        for (Path aPath : filesForDisplay) {

            String fileName;
            if (options.has("s"))
                fileName = aPath.toAbsolutePath().toString();
            else
                fileName = aPath.toString();

            if (options.has("l"))
                fileName = fileName.toLowerCase();


            System.out.println(fileName);
        }
    }
}