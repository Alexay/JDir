package szu.alex;

import joptsimple.OptionSet;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * This is the sorting method to be used after the filtered array is prepared.
 *
 * USED BY: Main.java
 */
public class O {

    public static Path[] sort(Path[] toSort, OptionSet options) {


        if ((options.valueOf("o") == "e") || (options.valueOf("o") == "E") ||
        (options.valueOf("o") == "-e") || (options.valueOf("o") == "-E")) {
            Arrays.sort(toSort, (path1, path2) -> {

                // We first need to make sure that either both files or neither file
                // has an extension (otherwise we'll end up comparing the extension of one
                // to the start of the other, or else throwing an exception).
                String string1 = path1.toString();
                String string2 = path2.toString();
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

        for (int i = 0; i < toSort.length; i++) {
            System.out.println(toSort[i].toString());
        }
        return toSort;
    }
}
