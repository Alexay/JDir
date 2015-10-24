package jdir;

/**
 * This is the wide output mode, designed to output bare-like filenames in a Unix-like fashion of multiple columns.
 *
 * Same as W, but outputs vertically instead of horizontally.
 *
 *
 * USED BY: Main.java
 */


import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;



public class D {
    public static void display(Path[] filesForDisplay, OptionSet options) {

        // We begin by initializing some counters for the footer stats.
        long freeDiskSpaceCounter = filesForDisplay[0].toFile().getUsableSpace();
        int dirCounter = 0;
        long sizeCounter = 0;

        // This block takes care of the header. The header reader method for some reason
        // displays the improper path if the given path is actually a directory,
        // so to circumvent that we only pass the path that is a file.
        Path pathToReadForHeader = filesForDisplay[0];
        for (Path aPath : filesForDisplay)
            if(!aPath.toFile().isDirectory()) {
                pathToReadForHeader = aPath;
                break;
            }
        // OK, let's print the header.
        HeaderDataReader.read(pathToReadForHeader);
        String[] outputStringArray = new String[filesForDisplay.length];
        ColumnFormatter output = new ColumnFormatter();

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
                    fileName  = ("[" + filesForDisplay[i].getFileName().toString() + "]");
                else
                    fileName  = filesForDisplay[i].getFileName().toString();

                // If the user used the "l" option, we convert the file name to lowercase characters.
                if (options.has("l"))
                    fileName = fileName.toLowerCase();
                //fileName = String.format("%1$" + 10 + "s", fileName);
                outputStringArray[i] = fileName;
            }
        } catch (IOException b) {
            System.err.println("W.java: "+b);
        }

        // Since our column formatter takes input in line-by-line, we need to transpose our lines into columns,
        // the way we do that is by first calculating the number of needed rows from the total number
        // of array elements, then dividing that by out constant number of columns, which is 3.
        // If the division isn't even, we add one to compensate for the remainder.
        int numberOfRows = outputStringArray.length%3==0?outputStringArray.length/3:outputStringArray.length/3+1;

        // This block prints out the columns and takes into account whether an array element exists.
        for (int i = 0; i<numberOfRows; i++)
            output.addLine(
                    outputStringArray[i],
                    i+numberOfRows >= outputStringArray.length?"":outputStringArray[i+numberOfRows],
                    i+numberOfRows*2 >= outputStringArray.length?"":outputStringArray[i+numberOfRows*2]
            );
        output.print();

        // After we outputted all the files, we output the footer.
        // Here we calculate the number of non-directory files.
        int nonDirCounter = filesForDisplay.length-dirCounter;

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
        }
        else {
            totalSize = Long.toString(sizeCounter);
            freeDiskSpace = Long.toString(freeDiskSpaceCounter);
        }
        totalSize = String.format("%1$" + 14 + "s", totalSize);
        freeDiskSpace = String.format("%1$" + 15 + "s", freeDiskSpace);

        // Printing the footer
        System.out.println(nonDirs + " File(s) " + totalSize + " bytes");
        System.out.println(dirs  + " Dir(s) " + freeDiskSpace + " bytes free");

    }
}