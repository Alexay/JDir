package com.alex;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.lang.reflect.Method;
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

        parser.allowsUnrecognizedOptions();
        parser.accepts("a", "Display all").withOptionalArg().withValuesSeparatedBy(',');
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("?", "Displays this help prompt");

        OptionSet options = parser.parse(args);



        //  In case the user specifies a path instead of just running the command locally, create an array out of the parsed directory strings.
        String[] dirStringArray = options.nonOptionArguments().toArray(new String[options.nonOptionArguments().size()]);

        // Then, we create an array of paths by converting each individual string into a path, but the user may enter the path incorrectly, and we must account for that exception.
        Path[] dirPathArray = new Path[dirStringArray.length];
        for (int k = 0; k < dirStringArray.length; k++) {
            try {
                dirPathArray[k] = Paths.get(dirStringArray[k]);
            } catch (InvalidPathException x) {
                System.out.println(dirStringArray[k] + " is an invalid path. Ignoring...");
            }
        }

        // If the user has the help options in any of the options parser the app just displays the help prompt.
        if (options.has("?")) {
            parser.printHelpOn(System.out);
        }

        //
        else {

            // This is the case if the user doesn't give any paths to the command and just wants to run in the local path
            if (options.nonOptionArguments().isEmpty()) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(localDir)) {
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

                        // Is it indexed?
                        boolean isIndexed = IndexedAttributeReader.read(attr);

                        // Let's parse the argument we got
                        List arguments = options.valuesOf("a");

                        // Now that we have all the commonly used variables initialized, we begin the "if" statement tree.
                        // Firstly, the display filtering option.
                        if (options.has("a")) {

                            // Does the option have any arguments for more specific filtering?
                            if (options.hasArgument("a")) {

                                // OK, it does, now we make the long print statement that will be different depending on the selected filtering arguments.
                                // This huge "if" statement will filter out unwanted files
                                if ((arguments.contains("d") && !isDir) || (arguments.contains("h") && !isHidden) || (arguments.contains("s") && !isSystem) || (arguments.contains("l") && !isJunction) || (arguments.contains("r") && !isReadonly) || (arguments.contains("a") && !isForArchiving))
                                    continue;
                            }

                            // Initializing time of last modification
                            FileTime lastModifiedFileTime = attr.lastModifiedTime();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            String lastModified = df.format(lastModifiedFileTime.toMillis());

                            // OK, we've initialized everything we need, let's print!
                            System.out.println(
                                    lastModified + "    " +
                                            (isDir ? (isJunction ? "<JUNCTION>  " : "<DIR>       ") : "          " + attr.size()) + " " +
                                            file.getFileName()
                            );

                        }
                        // The "Bare" option, lists everything raw
                        else if (options.has("b")) {
                            System.out.println(file.getFileName());
                        }

                        // If the user doesn't specify any arguments, the app will just run identically to the Windows DIR, not displaying hidden files or paths
                        else {
                            if (isHidden || isSystem)
                                continue;
                            // Initializing time of last modification
                            FileTime lastModifiedFileTime = attr.lastModifiedTime();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            String lastModified = df.format(lastModifiedFileTime.toMillis());
                            System.out.println(
                                    lastModified + "    " +
                                            (isDir ? (isJunction ? "<JUNCTION>  " : "<DIR>       ") : "          " + attr.size()) + " " +
                                            file.getFileName()
                            );
                        }
                    }

                } catch (IOException | DirectoryIteratorException x) {
                    System.err.println(x);
                }
            }

            // This is the case if the user actually provides a path.
            else {
                for (int k = 0; k<dirPathArray.length; k++) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPathArray[k])) {
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
                            boolean isJunction = false;
                            if (DosFileAttributes.class.isInstance(attr)) {
                                try {
                                    Method m = attr.getClass().getDeclaredMethod("isReparsePoint");
                                    m.setAccessible(true);
                                    isJunction = (boolean) m.invoke(attr);
                                } catch (Exception e) {
                                    // just gave it a try
                                }
                            }

                            // Is it read-only?
                            boolean isReadonly = attr.isReadOnly();

                            // Is it ready for archiving?
                            boolean isForArchiving = attr.isArchive();

                            // Is it indexed?
                            boolean isIndexed = false;
                            if (DosFileAttributes.class.isInstance(attr)) {
                                isIndexed = true;
                                try {
                                    Method m = attr.getClass().getDeclaredMethod("attributes");
                                    m.setAccessible(true);
                                    int attrs = (int) m.invoke(attr);
                                    isIndexed = ((attrs & 0x2000) == 0);
                                } catch (Exception e) {
                                    // just gave it a try
                                }
                            }

                            // Let's parse the argument we got
                            List arguments = options.valuesOf("a");

                            // Now that we have all the commonly used variables initialized, we begin the "if" statement tree.
                            // Firstly, the display filtering option.
                            if (options.has("a")) {

                                // Does the option have any arguments for more specific filtering?
                                if (options.hasArgument("a")) {

                                    // OK, it does, now we make the long print statement that will be different depending on the selected filtering arguments.
                                    // This huge "if" statement will filter out unwanted files
                                    if ((arguments.contains("d") && !isDir) || (arguments.contains("h") && !isHidden) || (arguments.contains("s") && !isSystem) || (arguments.contains("l") && !isJunction) || (arguments.contains("r") && !isReadonly) || (arguments.contains("a") && !isForArchiving) || (arguments.contains("i") && isIndexed))
                                        continue;
                                }

                                // Initializing time of last modification
                                FileTime lastModifiedFileTime = attr.lastModifiedTime();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String lastModified = df.format(lastModifiedFileTime.toMillis());



                                // OK, we've initialized everything we need, let's print!
                                System.out.println(
                                        lastModified + /*" " + test3 +*/ "    " +
                                                (isDir ? (isJunction ? "<JUNCTION>  " : "<DIR>       ") : "          " + attr.size()) + " " +
                                                file.getFileName()
                                );

                            }
                            // The "Bare" option, lists everything raw
                            else if (options.has("b")) {
                                System.out.println(file.getFileName());
                            }

                            // If the user doesn't specify any arguments, the app will just run identically to the Windows DIR, not displaying hidden files or paths
                            else {
                                if (isHidden || isSystem)
                                    continue;
                                // Initializing time of last modification
                                FileTime lastModifiedFileTime = attr.lastModifiedTime();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String lastModified = df.format(lastModifiedFileTime.toMillis());
                                System.out.println(
                                        lastModified + "    " +
                                                (isDir ? (isJunction ? "<JUNCTION>  " : "<DIR>       ") : "          " + attr.size()) + " " +
                                                file.getFileName()
                                );
                            }
                        }

                    } catch (IOException | DirectoryIteratorException x) {
                        System.err.println(x);
                    } catch (NullPointerException z) {
                    }
                }
            }
        }
    }
}
