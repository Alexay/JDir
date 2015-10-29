package jdir;

import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import static jdir.Main.linePrintSetting;

/**
 * This class is used for converting a single argument path into an ArrayList of recursive paths to be passed on to the
 * rest of the program.
 *
 * USED BY: Main.java
 */
public class S {

    // This is the method we use for getting an ArrayList of all the paths within a a path recursively.
    public static ArrayList<Path> listFiles(Path path) throws IOException {
        ArrayList<Path> recursivePathsArray = new ArrayList<>();

        // We need to make sure that the path being passed to the method is, in fact, a directory, otherwise the method
        // will throw an exception.
        if (!Files.isDirectory(path))
            return recursivePathsArray;

        recursivePathsArray.add(path);

        // Here's the recursive parsing block.
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream)
                if (Files.isDirectory(entry))
                    recursivePathsArray.addAll(listFiles(entry));
        } catch (AccessDeniedException ignore){}


        return recursivePathsArray;
    }

    // This is the method the Main function passes on the user-given path arguments for recursive parsing.
    public static void display(Path path, OptionSet options) throws IOException {
        ArrayList<Path> dirPathArray = listFiles(path);

        if (!options.has("b"))
            HeaderDataReader.read(dirPathArray.get(0));

        // For each given path we will perform the needed operations.
        for (int i = 0; i < dirPathArray.size(); i++) {

            Path pathToFilter = dirPathArray.get(i);
            ArrayList<Path> toArray = A.filter(pathToFilter, options);


            // Let's convert our ArrayList to a normal array to save some memory.
            Path[] toSortAndDisplay = new Path[toArray.size()];
            toArray.toArray(toSortAndDisplay);

            // Print the directory header if output isn't bare.
            if (!options.has("b"))
                P.printIt("Directory of " + pathToFilter.toFile().getCanonicalPath() + "\n", linePrintSetting);

            // The "Bare" display option, if specified, takes precedence over other display options.
            if (options.has("b"))
                B.display(O.sort(toSortAndDisplay, options), options);

                // The old-school win95/MS-DOS display option.
            else if (options.has("n")) {
                N.display(O.sort(toSortAndDisplay, options), options);
            }

            // Columns display option.
            else if (options.has("w")) {
                W.display(O.sort(toSortAndDisplay, options), options);
            }

            // Columns display option.
            else if (options.has("d")) {
                D.display(O.sort(toSortAndDisplay, options), options);
            }

            // If no other display options are specified, we default to the standard display.
            else {
                StandardDisplay.display(O.sort(toSortAndDisplay, options), options);
            }
        }
    }
}