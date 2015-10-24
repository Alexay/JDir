package jdir;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;

/**
 * This class serves as one of three display modes
 *
 * USED BY: Main.java
 */

public class StandardDisplay {
    public static void display(Path[] filesForDisplay, OptionSet options) {

        // We begin by initializing some counters for the footer stats.
        int dirCounter = 0;
        long sizeCounter = 0;

        // This block takes care of the header. The header reader method for some reason
        // displays the improper path if the given path is actually a directory,
        // so to circumvent that we only pass the path that is a file.
        Path pathToReadForHeader = Paths.get("");
        for (Path aPath : filesForDisplay)
            if(!aPath.toFile().isDirectory()) {
                pathToReadForHeader = aPath;
                break;
            }
        long freeDiskSpaceCounter = pathToReadForHeader.toFile().getUsableSpace();

        // OK, let's print the header.
        //HeaderDataReader.read(pathToReadForHeader);


        // If the user specified the "r" option, and didn't filter for certain
        // files, then we first display the ADS data.
        if (options.has("r") &&
                !(options.valuesOf("a").contains("h") ||
                options.valuesOf("a").contains("s") ||
                options.valuesOf("a").contains("-a") ||
                options.valuesOf("a").contains("r") ||
                options.valuesOf("a").contains("l"))
                )
            ADSReader.display(filesForDisplay[0].getParent());

        try {
            for (Path aPath : filesForDisplay) {
                // First, we'll initialize the different variables that may occur in the filtering.
               DosFileAttributes attr = Files.readAttributes(aPath, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Let's get our timestamp depending on the option given by the user.
                String timeStamp = T.parseTime(attr, options);
                timeStamp = String.format("%1$-" + 21 + "s" , timeStamp);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // If the path points to a directory, we also increment our directory counter for the output footer.
                if (isDir)
                    dirCounter++;
                else
                    sizeCounter += attr.size();

                // Is it reparse point / junction?
                boolean isJunction = ReparsePointAttributeReader.read(attr);

                // We initialize the fileName for the conditional steps.
                String fileName;
                if (isJunction)
                    fileName  = (aPath.getFileName().toString() + " " + "[" + aPath.toRealPath() + "]");
                else
                    fileName  = aPath.getFileName().toString();

                // This block deals with initializing the 8-dot-3 filename in case the user specifies the "X" option.
                String DOSfileName = Paths.get(N.getMSDOSName(aPath.toString())).getFileName().toString();
                DOSfileName = String.format("%1$-" + 13 + "s", DOSfileName);

                // If the user used the "l" option, we convert the file name to lowercase characters.
                if (options.has("l")) {
                    DOSfileName = DOSfileName.toLowerCase();
                    fileName = fileName.toLowerCase();
                }

                // This block deals with initializing the file size and putting it into the proper format.
                String fileSize;

                // If the user used the "c" option, we add separators between each thousand.
                if (options.has("c"))
                    fileSize = C.thousandSeparate(attr.size());
                else
                    fileSize = Long.toString(attr.size());

                fileSize =  String.format("%1$" + 15 + "s" , fileSize);

                // OK, we've initialized everything we need, let's print!
                System.out.println(
                        timeStamp +
                                (isDir ?
                                        (isJunction ?
                                                "<JUNCTION>" : "<DIR>     ") : "          ") +
                                fileSize + " " +
                                (options.has("x")?DOSfileName:"") +
                                fileName
                );
            }
        } catch (IOException | InterruptedException b) {
            System.err.println("StandardDisplay.java: "+b);
        }

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
