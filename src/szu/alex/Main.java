package szu.alex;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {

    public static void main(String[] args) throws Exception {
        Path localDir = Paths.get(".");

        OptionParser parser = new OptionParser("a::b?*");

        OptionSet options = parser.parse(args);

        parser.accepts("a", "Display all");
        parser.accepts("b", "Bare output without metadata");
        parser.accepts("?", "Displays this help prompt");

        if (options.has("?"))
            parser.printHelpOn(System.out);
        else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(localDir)) {

                // The display filtering option. This will check for other provided arguments
                if(options.has("a")) {
                    for (Path file : stream) {
                        System.out.println(file.getFileName());
                    }
                }

                // The "Bare" option, lists everything raw
                else if (options.has("b")) {
                    for (Path file : stream) {
                        System.out.println(file.getFileName());
                    }
                }

                // If the user doesn't specify any arguments, the app will just run identically to the Windows DIR
                else {
                    for (Path file : stream) {
                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
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