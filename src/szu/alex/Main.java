package szu.alex;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Path localDir = Paths.get(".");

        OptionParser parser = new OptionParser("a::b?*");

        OptionSet options = parser.parse(args);

        parser.accepts("a", "Display all").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("?", "Displays this help prompt");

        if (options.has("?"))
            parser.printHelpOn(System.out);
        else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(localDir)) {

                // The display filtering option.
                if(options.has("a")) {

                    // Does the option have any arguments for more narrow filtering?
                    if (options.hasArgument("a")) {
                        // OK, it does, now we make the long print statement that will be different depending on the selected filtering arguments.
                        for (Path file : stream) {
                            // First, we'll initialize the different variables that may occur in the filtering
                            DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);

                            // Is it a directory?
                            boolean isDir = attr.isDirectory();

                            // Is it hidden or system file?
                            boolean isHidden = attr.isHidden();

                            //Is it a system file?
                            boolean isSystem = attr.isSystem();

                            // Is it reparse point / junction?
                            boolean isJunction = attr.isSymbolicLink();

                            // Is it read-only?
                            boolean isReadonly = attr.isReadOnly();

                            // Is it ready for archiving?
                            boolean isForArchiving = attr.isArchive();

                            // Let's parse the argument we got
                            List arguments = options.valuesOf("a");

                            for (int g = 0; g<arguments.size(); g++)
                                System.out.println(arguments.get(g));

                            // This huge "if" statement will filter out unwanted files
                            if ((arguments.contains("d") && !isDir) || (arguments.contains("h") && !isHidden) || (arguments.contains("s") && !isSystem) || (arguments.contains("l") && !isJunction) || (arguments.contains("r") && !isReadonly) || (arguments.contains("a") && !isForArchiving))
                                continue;

                            // Initializing time of last modification
                            FileTime lastModifiedFileTime = attr.lastModifiedTime();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            String lastModified = df.format(lastModifiedFileTime.toMillis());


                            // OK, we've initialized everything we need, let's print!
                            System.out.println(
                                    lastModified + "    " +
                                            (isDir?"<DIR>       ":"          " + attr.size()) + " " +
                                            file.getFileName());
                        }
                    }

                    // If it doesn't then just display everything
                    else {
                        for (Path file : stream) {
                            System.out.println(file.getFileName());
                        }
                    }
                }
                // The "Bare" option, lists everything raw
                else if (options.has("b")) {
                    for (Path file : stream) {
                        System.out.println(file.getFileName());
                    }
                }

                // If the user doesn't specify any arguments, the app will just run identically to the Windows DIR, not displaying hidden files or paths
                else {
                    for (Path file : stream) {
                        DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);

                        // Initializing time of last modification
                        FileTime lastModifiedFileTime = attr.lastModifiedTime();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        String lastModified = df.format(lastModifiedFileTime.toMillis());
                        System.out.println(lastModified + "     " + file.getFileName());
                    }
                }

            } catch (IOException | DirectoryIteratorException x) {
                System.err.println(x);
            }
        }


    }
}