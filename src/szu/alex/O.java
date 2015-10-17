package szu.alex;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Arrays;
import java.util.List;

/**
 * This is the sorting method to be used after the filtered array is prepared.
 *
 * USED BY: Main.java
 */
public class O {

    public static Path[] sort(Path[] toSort, OptionSet options) {

        List arguments = options.valuesOf("o");

        // This is the algorithm for sorting by name. Since the array is already sorted
        // alphabetically by default, we only need to account for the hyphenated argument.
        if (arguments.contains("n") || arguments.contains("-n")) {
            Arrays.sort(toSort, (path1, path2) -> {

                if (arguments.contains("-n"))
                    return path2.compareTo(path1);
                else
                    return path1.compareTo(path2);
            });
        }

        // This is the algorithm for sorting by extension.
        else if (arguments.contains("e") || arguments.contains("-e")) {
            Arrays.sort(toSort, (path1, path2) -> {

                // Reverse the sorting if hyphenated argument was passed.
                if (arguments.contains("-e")) {
                    Path tmp = path1;
                    path1 = path2;
                    path2 = tmp;
                }

                // Convert the paths to strings for comparator.
                String string1 = path1.toString();
                String string2 = path2.toString();

                // We first need to make sure that either both files or neither file
                // has an extension (otherwise we'll end up comparing the extension of one
                // to the start of the other, or else throwing an exception).
                final int s1Dot = string1.lastIndexOf('.');
                final int s2Dot = string2.lastIndexOf('.');
                if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
                    string1 = string1.substring(s1Dot + 1);
                    string2 = string2.substring(s2Dot + 1);
                    return string1.compareTo(string2);
                } else if (s1Dot == -1) { // only string2 has an extension, so string1 goes first
                    return -1;
                } else { // only s1 has an extension, so s1 goes second
                    return 1;
                }
            });
        }

        // This is the algorithm for sorting by size.
        else if (arguments.contains("s") || arguments.contains("-s")) {
            Arrays.sort(toSort, (p1, p2) -> {
                if (arguments.contains("-s"))
                    return Long.compare(p2.toFile().length(),p1.toFile().length());
                return Long.compare(p1.toFile().length(),p2.toFile().length());
            });
        }

        // This is the algorithm for sorting by date. We also need to take into
        // consideration the "T" option that selects the displayed and sorted
        // time attribute.
        else if (arguments.contains("d") || arguments.contains("-d")) {
            Arrays.sort(toSort,  (Path path1, Path path2) -> {
                try {
                    if (options.has("t")) {
                        DosFileAttributes attr1 = Files.readAttributes(path1, DosFileAttributes.class);
                        DosFileAttributes attr2 = Files.readAttributes(path2, DosFileAttributes.class);
                        String timeArgument = options.valueOf("t").toString();

                        switch (timeArgument) {
                            case "c":
                                if (arguments.contains("-d"))
                                    return (attr2.creationTime().compareTo(attr1.creationTime()));
                                else
                                    return (attr1.creationTime().compareTo(attr2.creationTime()));
                            case "a":
                                if (arguments.contains("-d"))
                                    return (attr2.lastAccessTime().compareTo(attr1.lastAccessTime()));
                                else
                                    return (attr1.lastAccessTime().compareTo(attr2.lastAccessTime()));
                            default:
                                if (arguments.contains("-d"))
                                    return (attr2.lastModifiedTime().compareTo(attr1.lastModifiedTime()));
                                else
                                    return (attr1.lastModifiedTime().compareTo(attr2.lastModifiedTime()));
                        }
                    }
                    else {
                        if (arguments.contains("-d"))
                            return Long.compare(path2.toFile().lastModified(), path1.toFile().lastModified());
                        else
                            return Long.compare(path1.toFile().lastModified(), path2.toFile().lastModified());
                    }
                } catch (IOException x) {
                    System.err.println("O sorting function -d argument " + x);
                }
                return 0;
            });
        }


        // This is the algorithm for directory grouped sorting.
        if (arguments.contains("g") || arguments.contains("-g")) {
            Arrays.sort(toSort, (path1, path2) -> {

                if (arguments.contains("g"))
                    return Boolean.compare(Files.isDirectory(path2), Files.isDirectory(path1));
                else
                    return Boolean.compare(Files.isDirectory(path1), Files.isDirectory(path2));
            });
        }

        return toSort;
    }
}
