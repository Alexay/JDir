package jdir;

import jdir.util.HeaderDataReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {

    public static int linePrintSetting = -10;

    public static void main(String[] args) throws Exception {
        Path localDir = Paths.get(".");

        // Convert the user input to lowercase for easier parsing and simpler data flow.
        for (int iter = 0; iter < args.length; iter++)
            args[iter] = args[iter].toLowerCase();

        // This initializes which options the parser accepts. If the option
        // has ":" after it, it means that it accepts one argument, if "::"
        // then it accepts multiple arguments.
        OptionParser parser = new OptionParser("a::o::bwdrsqpxnc4t:l?*");

        // Detailing of the accepted options with some specifications.
        parser.accepts("a", "Displays files with specified attributes.").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("o", "List by files in sorted order.").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("t", "Time").withOptionalArg().defaultsTo("w");
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("4", "Displays four-digit years.");
        parser.accepts("w", "Uses wide list format.");
        parser.accepts("l", "Uses lowercase.");
        parser.accepts("n", "MS-DOS style DIR display format.");
        parser.accepts("p", "Pauses after each specified amount of lines for easier reading.");
        parser.accepts("q", "Display the owner of the file.");
        parser.accepts("r", "Enables display of alternate data stream data.");
        parser.accepts("s", "Displays files in specified directory and all subdirectories.");
        parser.accepts("x", "This displays the short names generated for non-8dot3 filenames." +
                " The format is that of the standard format with the short name inserted" +
                " before the long name. If no short name is present, blanks are" +
                " displayed in its place.");
        parser.accepts("d", "Same as wide but files are list sorted by column.");
        parser.accepts("c", "Enables thousand separators in the file size output");
        parser.accepts("?", "Displays this help prompt");
        parser.nonOptions("Paths to be processed. Wildcards may be used. If no paths are given, the application runs " +
                "in the local directory.");

        OptionSet options = parser.parse("");
        try {
            options = parser.parse(args);
        } catch (OptionException x) {
            System.out.println("One or more of the options you have specified is not valid. Try using the option " +
                    "-? to display the help prompt.");
            System.exit(-1);
        }
        if (options.has("p")) {
            while (true) {
                try {
                    System.out.println("Please input how many lines you would like to output:");
                    Scanner scan = new Scanner(System.in);
                    linePrintSetting = scan.nextInt();
                    if (linePrintSetting <= 0)
                        System.out.println("Invalid input. Please try again.");
                    else
                        break;
                } catch (InputMismatchException x) {
                    System.out.println("Invalid input. Please try again.");
                }
            }
        }

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
                    else {
                    }
                else
                    System.out.println("File \"" + options.nonOptionArguments().get(i).toString() + "\" not found.\n");
            } catch (InvalidPathException x) {
                System.out.println(options.nonOptionArguments().get(i).toString() + " is an invalid path. Ignoring...");
            }
        }

        // If the user has the help options in any of the options parser the app just displays the help prompt.
        if (options.has("?")) {
            System.out.println("Displays a list of files and subdirectories in a directory.\n" +
                    "\n" +
                    "Usage: [path] [-A[[=]attribute[,attribute]] [-B] [-C] [-D] [-L] [-N]\n" +
                    "  [-O[[=]sortorder[,sortorder]] [-P] [-Q] [-R] [-S] [-T[[=]timefield]] [-W] [-X] [-4]\n\n" +
                    "Examples of usage:\n" +
                    "java jdir.Main C:\\ -a=d,h,l,i,-r -clnpqs4\n" +
                    "java jdir.Main -bo=s\n" +
                    "java jdir.Main C:\\Users\\larry C:\\Users\\john -c -4 -s -p -q -t=w -o=d,g\n");
            System.out.println("-A          Displays files with specified attributes.\n" +
                    "attributes   D  Directories                R  Read-only files\n" +
                    "             H  Hidden files               A  Files ready for archiving\n" +
                    "             S  System files               I  Not content indexed files\n" +
                    "             L  Reparse Points             -  Prefix meaning not\n" +
                    "-B          Uses bare format (no heading information or summary).\n" +
                    "-C          Display the thousand separator in file sizes.\n" +
                    "-D          Same as wide but files are list sorted by column.\n" +
                    "-L          Uses lowercase.\n" +
                    "-N          MS-DOS style DIR display format.\n" +
                    "-O          List by files in sorted order.\n" +
                    "sortorder    N  By name (alphabetic)       S  By size (smallest first)\n" +
                    "             E  By extension (alphabetic)  D  By date/time (oldest first)\n" +
                    "             G  Group directories first    -  Prefix to reverse order\n" +
                    "-P          Pauses after each specified amount of lines for easier reading.\n" +
                    "-Q          Display the owner of the file.\n" +
                    "-R          Display alternate data streams of the file.\n" +
                    "-S          Displays files in specified directory and all subdirectories.\n" +
                    "-T          Controls which time field displayed or used for sorting\n" +
                    "timefield   C  Creation\n" +
                    "            A  Last Access\n" +
                    "            W  Last Written\n" +
                    "-W          Uses wide list format.\n" +
                    "-X          This displays the short names generated for non-8dot3 file\n" +
                    "            names.  The short name is inserted\n" +
                    "            before the long name. If no short name is present, blanks are\n" +
                    "            displayed in its place.\n" +
                    "-4          Displays four-digit years");
        }

        // Now that we've handled the "help" option, let's get down to business!
        else {

            // This is the case if the user doesn't give any paths to the command and just wants to run in the local path
            if (options.nonOptionArguments().isEmpty() && !options.has("s")) {

                ArrayList<Path> toArray = A.filter(localDir, options);

                // Let's convert our ArrayList to a normal array to save some memory.
                Path[] toSortAndDisplay = new Path[toArray.size()];
                toArray.toArray(toSortAndDisplay);

                // Print the directory header if output isn't bare.
                if (!options.has("b")) {
                    HeaderDataReader.read(localDir);
                    P.printIt("Directory of " + localDir.toFile().getCanonicalPath() + "\n", linePrintSetting);
                }

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
                    P.printIt("Non of the given paths are valid for displaying. \n" +
                            "Due to the limitations of Java and the Windows command shell this application only accepts directories as arguments.\n" +
                            "The program will now exit.", linePrintSetting);


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

                        if (!options.has("b")) {
                            HeaderDataReader.read(pathToFilter);
                            P.printIt("Directory of " + pathToFilter.toFile().getCanonicalPath() + "\n", linePrintSetting);
                        }

                        // The "Bare" display option, if specified, takes precedence over other display options.
                        if (options.has("b"))
                            B.display(O.sort(toSortAndDisplay, options), options);

                            // The old-school win95/MS-DOS display option.
                        else if (options.has("n"))
                            N.display(O.sort(toSortAndDisplay, options), options);

                            // Columns display option.
                        else if (options.has("w"))
                            W.display(O.sort(toSortAndDisplay, options), options);

                            // Columns display option.
                        else if (options.has("d"))
                            D.display(O.sort(toSortAndDisplay, options), options);

                            // If no other display options are specified, we default to the standard display.
                        else
                            StandardDisplay.display(O.sort(toSortAndDisplay, options), options);
                    }
                }
            }
        }
    }
}