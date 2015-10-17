package com.alex;

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

        int dirCounter = 0;

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

                // Is it reparse point / junction?
                boolean isJunction = ReparsePointAttributeReader.read(attr);


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
                                        (isJunction ?
                                                "<JUNCTION>" : "<DIR>     ") : "          ") +
                                fileSize + " " +
                                timeStamp
                );
            }
        } catch (IOException b) {
            b.printStackTrace();
        } catch (InterruptedException ignored) {}

        // Here we initializer the directory
        int nonDirCounter = filesForDisplay.length-dirCounter;
        System.out.println(dirCounter);
        System.out.println(nonDirCounter);
    }
}
