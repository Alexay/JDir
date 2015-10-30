package jdir;

import jdir.util.IndexedAttributeReader;
import jdir.util.ReparsePointAttributeReader;
import jdir.util.joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the filter that allows the user to only display certain files
 *
 * USED BY: Main.java
 **/


class A {
    public static ArrayList<Path> filter(Path dirPath, OptionSet options) {
        ArrayList<Path> filteredFileArray = new ArrayList<>();


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path file : stream) {

                // First, we'll initialize the different variables that may occur in the filtering
                DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // Is it hidden?
                boolean isHidden = attr.isHidden();

                //Is it a system file?
                boolean isSystem = attr.isSystem();

                // Is it reparse point / junction? This one is impossible to implement without invoking the
                boolean isJunction = ReparsePointAttributeReader.read(attr);

                // Is it read-only?
                boolean isReadonly = attr.isReadOnly();

                // Is it ready for archiving?
                boolean isForArchiving = attr.isArchive();

                // Is it indexed?
                boolean isIndexed = IndexedAttributeReader.read(attr);


                // If neither options nor arguments are given, we filter for the default configuration.
                if (!options.has("a")) {
                    if (!isHidden)
                        filteredFileArray.add(file);
                }

                // We check if the user gave this option any arguments, and filter accordingly.
                else if (options.hasArgument("a")) {
                    List arguments = options.valuesOf("a");

                    // This huge "if" statement will filter out unwanted files and account for when a user enters
                    // mutually exclusive arguments such as h and -h at the same time.
                    if (!(((arguments.contains("d") && !isDir) || (arguments.contains("-d") && isDir)) != (arguments.contains("d") && arguments.contains("-d")) ||
                            ((arguments.contains("h") && !isHidden) || (arguments.contains("-h") && isHidden)) != (arguments.contains("h") && arguments.contains("-h")) ||
                            ((arguments.contains("s") && !isSystem) || (arguments.contains("-s") && isSystem)) != (arguments.contains("s") && arguments.contains("-s")) ||
                            ((arguments.contains("l") && !isJunction) || (arguments.contains("-l") && isJunction)) != (arguments.contains("l") && arguments.contains("-l")) ||
                            ((arguments.contains("r") && !isReadonly) || (arguments.contains("-r") && isReadonly)) != (arguments.contains("r") && arguments.contains("-r")) ||
                            ((arguments.contains("a") && !isForArchiving) || (arguments.contains("-a") && isForArchiving)) != (arguments.contains("a") && arguments.contains("-a")) ||
                            ((arguments.contains("i") && isIndexed) || (arguments.contains("-i") && !isIndexed)) != (arguments.contains("i") && arguments.contains("-i"))
                            ))
                        filteredFileArray.add(file);
                }

                // If no arguments are given to the "a" option, we don't filter at all.
                else
                    filteredFileArray.add(file);
            }
        } catch (NotDirectoryException | NullPointerException ignored) {} catch (IOException x) {
            System.err.println(x + " A.java");
        }
        return filteredFileArray;
    }
}