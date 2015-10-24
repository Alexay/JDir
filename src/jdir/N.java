package jdir;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;

/**
 * This is the display mode for the MS-DOS/Win95 style 8.3 filenames with their names on the left.
 *
 * USED BY: Main.java
 */

public class N {

    public static String getMSDOSName(String fileName)
            throws IOException, InterruptedException {

        String path = fileName;

        Process process =
                Runtime.getRuntime().exec(
                        "cmd /c for %I in (\"" + path + "\") do @echo %~fsI");

        process.waitFor();

        byte[] data = new byte[65536];
        int size = process.getInputStream().read(data);

        if (size <= 0)
            return null;

        return new String(data, 0, size).replaceAll("\\r\\n", "");
    }

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


        try {
            for (Path aPath : filesForDisplay) {
                // First, we'll initialize the different variables that may occur in the filtering
                DosFileAttributes attr = Files.readAttributes(aPath, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Let's get our timestamp depending on the option given by the user.
                String timeStamp = T.parseTime(attr, options);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // If the path points to a directory, we also increment our directory counter for the output footer.
                if (isDir)
                    dirCounter++;


                // In this statement we take the full normal path, convert it to a string, pass it to be converted
                // into the 8-dot-3 format, get it returned in string format, convert that string into a Path,
                // isolate the filename of the Path, convert it to a string and finally format that string.
                String fileName = Paths.get(getMSDOSName(aPath.toString())).getFileName().toString();
                fileName = String.format("%1$-" + 13 + "s", fileName);

                // If the user used the "l" option, we convert the file name to lowercase characters.
                if (options.has("l"))
                    fileName = fileName.toLowerCase();


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
                        fileName +
                                (isDir ?
                                        "<DIR>     " : "          ") +
                                fileSize + " " +
                                timeStamp
                );
            }
        } catch (IOException b) {
            b.printStackTrace();
        } catch (InterruptedException ignored) {}

        // Here we initializer the directory
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

        System.out.println();
    }
}
