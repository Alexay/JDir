package szu.alex;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * This class serves as one of three display modes
 */

public class StandardDisplay {
    public void display(ArrayList<Path> filesForDisplay, OptionSet options) {

        try {
            for (Path aPath : filesForDisplay) {
                // First, we'll initialize the different variables that may occur in the filtering
               DosFileAttributes attr = Files.readAttributes(aPath, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                
                // Let's get our timestamp depending on the option given by the user.
                String timeStamp = T.parseTime(attr, options);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // Is it reparse point / junction?
                boolean isJunction = ReparsePointAttributeReader.read(attr);


                // OK, we've initialized everything we need, let's print!
                System.out.println(
                        timeStamp + "    " +
                                (isDir ? (isJunction ? "<JUNCTION>  " : "<DIR>       ") : "          " + attr.size()) + " " +
                                aPath.getFileName()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
