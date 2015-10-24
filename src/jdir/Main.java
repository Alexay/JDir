package jdir;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws Exception {
        Path localDir = Paths.get(".");

        // Convert the user input to lowercase for easier parsing and simpler data flow.
        for (int iter = 0; iter < args.length; iter++)
            args[iter] = args[iter].toLowerCase();

        OptionParser parser = new OptionParser("a::o::bwdrspxnc4t:l?*");

        parser.accepts("a", "Display all").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("o", "Sort").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("t", "Time").withOptionalArg().defaultsTo("w");
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("w", "Displays files in neat columns");
        parser.accepts("c", "Enables thousand separators in the file size output");
        parser.accepts("?", "Displays this help prompt");
        parser.nonOptions("files to be processed");

        OptionSet options = parser.parse(args);

        if (options.has("p")) {
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].replaceAll("-p", "");
            }
            P.display(args);
        } else {

            // In case the user specifies a path instead of just running the command locally,
            // create an array out of the parsed directory strings.
            // Then, we create an array of paths by converting each individual string into a path,
            // but the user may enter the path incorrectly, and we must account for that exception.
            // We also create a separate array to hold wildcard paths.
            ArrayList<Path> dirPathArray = new ArrayList<>();
            for (int i = 0; i < options.nonOptionArguments().size(); i++) {
                try {
                    if (Files.exists(Paths.get(options.nonOptionArguments().get(i).toString())))
                        if (Files.isDirectory(Paths.get(options.nonOptionArguments().get(i).toString())))
                            dirPathArray.add(Paths.get(options.nonOptionArguments().get(i).toString()));
                        else {}
                    else
                        System.out.println("File \"" + options.nonOptionArguments().get(i).toString() + "\" not found.\n");
                } catch (InvalidPathException x) {
                    System.out.println(options.nonOptionArguments().get(i).toString() + " is an invalid path. Ignoring...");
                }
            }

            // If the user has the help options in any of the options parser the app just displays the help prompt.
            if (options.has("?")) {
                parser.printHelpOn(System.out);
            }


            // Now that we've handled the "help" option, let's get down to business!
            else {

                // This is the case if the user doesn't give any paths to the command and just wants to run in the local path
                if (options.nonOptionArguments().isEmpty() && !options.has("s")) {

                    ArrayList<Path> toArray = A.filter(localDir, options);

                    // Let's convert our ArrayList to a normal array to save some memory.
                    Path[] toSortAndDisplay = new Path[toArray.size()];
                    toArray.toArray(toSortAndDisplay);

                    // The "Bare" display option, if specified, takes precedence over other display options.
                    if (options.has("b"))
                        B.display(O.sort(toSortAndDisplay, options), options);

                        // Columns display option.
                    else if (options.has("w"))
                        W.display(O.sort(toSortAndDisplay, options), options);

                        // Columns display option.
                    else if (options.has("d"))
                        W.display(O.sort(toSortAndDisplay, options), options);

                        // The old-school win95/MS-DOS display option.
                    else if (options.has("n"))
                        N.display(O.sort(toSortAndDisplay, options), options);

                        // If no other display options are specified, we default to the standard display.
                    else
                        StandardDisplay.display(O.sort(toSortAndDisplay, options), options);

                }

                // This is the case if the user actually provides a path and/or if the recursive option is set.
                else {

                    if (options.has("s") && options.nonOptionArguments().isEmpty())
                        dirPathArray.addAll(S.listFiles(localDir));

                    // In case the user did pass paths but none of them turned out to be valid, we print that
                    // information to the user.
                    else if (dirPathArray.isEmpty())
                        System.out.println("Non of the given paths are valid for displaying. \n" +
                                "Due to the limitations of Java and the Windows command shell this application only accepts directories as arguments.\n" +
                                "The program will now exit.");


                    // For each given path we will perform the needed operations.
                    for (int i = 0; i < dirPathArray.size(); i++) {

                        // Special case for recursive processing.
                        if (options.has("s"))
                            S.display(dirPathArray.get(i), options);

                        // Normal processing.
                        else {
                            Path pathToFilter = dirPathArray.get(i);
                            ArrayList<Path> toArray = A.filter(pathToFilter, options);

                            // Let's convert our ArrayList to a normal array to save some memory.
                            Path[] toSortAndDisplay = new Path[toArray.size()];
                            toArray.toArray(toSortAndDisplay);

                            // The "Bare" display option, if specified, takes precedence over other display options.
                            if (options.has("b"))
                                B.display(O.sort(toSortAndDisplay, options), options);

                                // The old-school win95/MS-DOS display option.
                            else if (options.has("n")) {
                                HeaderDataReader.read(pathToFilter);
                                N.display(O.sort(toSortAndDisplay, options), options);
                            }

                            // Columns display option.
                            else if (options.has("w")) {
                                HeaderDataReader.read(pathToFilter);
                                W.display(O.sort(toSortAndDisplay, options), options);
                            }

                            // Columns display option.
                            else if (options.has("d")) {
                                HeaderDataReader.read(pathToFilter);
                                D.display(O.sort(toSortAndDisplay, options), options);
                            }

                            // If no other display options are specified, we default to the standard display.
                            else {
                                HeaderDataReader.read(pathToFilter);
                                StandardDisplay.display(O.sort(toSortAndDisplay, options), options);
                            }
                        }
                    }
                }
            }
        }
    }
}