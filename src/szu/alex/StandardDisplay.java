package szu.alex;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;

/**
 * This class serves as one of three display modes
 */

public class StandardDisplay {
    public static void display(Path[] filesForDisplay, OptionSet options) {

        int dirCounter = 0;

        try {
            for (Path aPath : filesForDisplay) {
                // First, we'll initialize the different variables that may occur in the filtering
               DosFileAttributes attr = Files.readAttributes(aPath, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Let's get our timestamp depending on the option given by the user.
                String timeStamp = T.parseTime(attr, options);
                timeStamp = String.format("%1$-" + 21 + "s" , timeStamp);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // If the path points to a directory, we also increment our directory counter for the output footer.
                if (isDir)
                    dirCounter++;

                // Is it reparse point / junction?
                boolean isJunction = ReparsePointAttributeReader.read(attr);

                // We initialize the fileName for the conditional steps.
                String fileName = aPath.getFileName().toString();

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
                        timeStamp +
                                (isDir ?
                                        (isJunction ?
                                                "<JUNCTION>" : "<DIR>     ") : "          ") +
                                fileSize + " " +
                                fileName
                );
            }
        } catch (IOException b) {
            b.printStackTrace();
        }

        // Here we initializer the directory
        int nonDirCounter = filesForDisplay.length-dirCounter;
        System.out.println(dirCounter);
        System.out.println(nonDirCounter);

    }
}
