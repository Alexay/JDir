package szu.alex;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.file.*;
import java.util.ArrayList;


public class Test {

    public static void main(String[] args) throws Exception {
        Path localDir = Paths.get(".");
        OptionParser parser = new OptionParser("a::bwdc4t:l?*");

        parser.allowsUnrecognizedOptions();
        parser.accepts("a", "Display all").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("?", "Displays this help prompt");

        OptionSet options = parser.parse(args);

        // In case the user specifies a path instead of just running the command locally,
        // create an array out of the parsed directory strings.
        // Then, we create an array of paths by converting each individual string into a path,
        // but the user may enter the path incorrectly, and we must account for that exception.
        ArrayList<Path> dirPathArray = new ArrayList<>();
        for (int i = 0; i < options.nonOptionArguments().size() ; i++) {
            try {
                dirPathArray.add(Paths.get(options.nonOptionArguments().get(i).toString()));
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
            if (options.nonOptionArguments().isEmpty()) {
                ArrayList<Path> toArray = A.filter(localDir, options);

                // Let's convert our ArrayList to a normal array to save some memory.
                Path[] toSortAndDisplay = new Path[toArray.size()];
                toArray.toArray(toSortAndDisplay);

                if (options.has("b"))
                    B.display(toSortAndDisplay, options);

                else
                    StandardDisplay.display(toSortAndDisplay, options);
            }

            // This is the case if the user actually provides a path.
            else {

                // For each given path we will filter.
                for (int i = 0; i < dirPathArray.size(); i++) {
                    Path pathToFilter = dirPathArray.get(i);
                    ArrayList<Path> toArray = A.filter(pathToFilter, options);

                    // Let's convert our ArrayList to a normal array to save some memory.
                    Path[] toSortAndDisplay = new Path[toArray.size()];
                    toArray.toArray(toSortAndDisplay);

                    // The "Bare" display option, if specified, takes precedence over other display options.
                    if (options.has("b"))
                        B.display(O.sort(toSortAndDisplay, options), options);

                    // If no other display options are specified, we default to the standard display.
                    else
                        StandardDisplay.display(O.sort(toSortAndDisplay, options), options);
                }
            }
        }
    }
}
