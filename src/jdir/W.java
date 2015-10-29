package jdir;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import static jdir.Main.linePrintSetting;

/**
 * This is the wide output mode, designed to output bare-like filenames in a Unix-like fashion of multiple columns.
 *
 *
 * USED BY: Main.java
 */


public class W {
    public static void display(Path[] filesForDisplay, OptionSet options) {

        // We begin by initializing some counters for the footer stats.
        int dirCounter = 0;
        long sizeCounter = 0;
        String[] outputStringArray = new String[filesForDisplay.length];
        ColumnFormatter output = new ColumnFormatter();

        // This block takes care of the header. The header reader method for some reason
        // displays the improper path if the given path is actually a directory,
        // so to circumvent that we only pass the path that is a file.
        Path pathToReadForHeader = Paths.get(".");
        for (Path aPath : filesForDisplay)
            if (!aPath.toFile().isDirectory()) {
                pathToReadForHeader = aPath;
                break;
            }
        long freeDiskSpaceCounter = pathToReadForHeader.toFile().getUsableSpace();


        try {
            for (int i = 0; i < filesForDisplay.length; i++) {
                // First, we'll initialize the different variables that may occur in the filtering.
                DosFileAttributes attr = Files.readAttributes(filesForDisplay[i], DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // If the path points to a directory, we also increment our directory counter for the output footer.
                if (isDir)
                    dirCounter++;
                else
                    sizeCounter += attr.size();

                // We initialize the fileName for the conditional steps.
                String fileName;
                if (isDir)
                    fileName = ("[" + filesForDisplay[i].getFileName().toString() + "]");
                else
                    fileName = filesForDisplay[i].getFileName().toString();

                // If the user used the "l" option, we convert the file name to lowercase characters.
                if (options.has("l"))
                    fileName = fileName.toLowerCase();
                //fileName = String.format("%1$" + 10 + "s", fileName);
                outputStringArray[i] = fileName;
            }
        } catch (IOException b) {
            System.err.println("W.java: " + b);
        }

        // This block prints out the columns and takes into account whether an array element exists.
        for (int i = 0; i < outputStringArray.length; i += 3)
            output.addLine(
                    outputStringArray[i],
                    i + 1 >= outputStringArray.length ? "" : outputStringArray[i + 1],
                    i + 2 >= outputStringArray.length ? "" : outputStringArray[i + 2]
                    //i + 3 >= outputStringArray.length ? "" : outputStringArray[i + 3]
            );
        output.print();

        // After we outputted all the files, we output the footer.
        // Here we calculate the number of non-directory files.
        int nonDirCounter = filesForDisplay.length - dirCounter;

        // Here we format the dir and non-dir counters into padded string for display
        String nonDirs = String.format("%1$" + 15 + "s", nonDirCounter);
        String dirs = String.format("%1$" + 15 + "s", dirCounter);

        // In this block we prepare the total size of all the non-dir files in the path as strings for display
        // as well as the free disk space.
        String totalSize;
        String freeDiskSpace;
        if (options.has("c")) {
            totalSize = C.thousandSeparate(sizeCounter);
            freeDiskSpace = C.thousandSeparate(freeDiskSpaceCounter);
        } else {
            totalSize = Long.toString(sizeCounter);
            freeDiskSpace = Long.toString(freeDiskSpaceCounter);
        }
        totalSize = String.format("%1$" + 14 + "s", totalSize);
        freeDiskSpace = String.format("%1$" + 15 + "s", freeDiskSpace);

        // Printing the footer
        P.printIt(nonDirs + " File(s) " + totalSize + " bytes", linePrintSetting);
        P.printIt(dirs + " Dir(s) " + freeDiskSpace + " bytes free", linePrintSetting);

        System.out.println();

    }
}
