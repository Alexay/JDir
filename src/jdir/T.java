package jdir;

import joptsimple.OptionSet;

import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;

/**
 * This class is the T option, that allows to base the timestamp upon last access, creation, writing time or default to modification time
 *
 * USED BY: StandardDisplay.java
 */

class T {

    public static String parseTime(DosFileAttributes attr, OptionSet options) {

//        if (!options.has("t") || (options.has("t") && !options.hasArgument("t"))) {
//            FileTime lastModifiedFileTime = attr.lastModifiedTime();
//            DateFormat df1 = options.has("4")?DateFormat.getDateInstance(DateFormat.MEDIUM):DateFormat.getDateInstance(DateFormat.SHORT);
//            DateFormat df2 = DateFormat.getTimeInstance(DateFormat.SHORT);
//            String lastModified = df1.format(lastModifiedFileTime.toMillis()) + " " + df2.format(lastModifiedFileTime.toMillis());
//            return lastModified;
//        }
        String argument = options.valueOf("t").toString();

        // If the user provided no "t" option, or provided no argument to the "t" option
        // or has provided the "w" argument, we return the default timestamp - last modified.
        switch (argument) {
            case "w": {
                FileTime lastModifiedFileTime = attr.lastModifiedTime();
                DateFormat df1 = options.has("4") ? DateFormat.getDateInstance(DateFormat.MEDIUM) : DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat df2 = DateFormat.getTimeInstance(DateFormat.SHORT);
                String lastModified = df1.format(lastModifiedFileTime.toMillis()) + " " + df2.format(lastModifiedFileTime.toMillis());
                return lastModified;
            }

            // If the user provided the "c" argument, we return the creation time.
            case "c": {
                FileTime creationFileTime = attr.creationTime();
                DateFormat df1 = options.has("4") ? DateFormat.getDateInstance(DateFormat.MEDIUM) : DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat df2 = DateFormat.getTimeInstance(DateFormat.SHORT);
                String created = df1.format(creationFileTime.toMillis()) + " " + df2.format(creationFileTime.toMillis());
                return created;
            }

            // If the user provided the "a" argument, we return the last access time.
            default: { //if (options.valueOf("t") == "a")
                FileTime lastAccessFileTime = attr.lastAccessTime();
                DateFormat df1 = options.has("4") ? DateFormat.getDateInstance(DateFormat.MEDIUM) : DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat df2 = DateFormat.getTimeInstance(DateFormat.SHORT);
                String lastAccessed = df1.format(lastAccessFileTime.toMillis()) + " " + df2.format(lastAccessFileTime.toMillis());
                return lastAccessed;
            }
        }
    }
}
