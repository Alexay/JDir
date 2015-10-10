package szu.alex;

import com.martiansoftware.jsap.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {
//lol
    public static void main(String[] args) throws Exception {
        JSAP jsap = new JSAP();
        UnflaggedOption filepath = new UnflaggedOption("Path")
                .setStringParser(JSAP.STRING_PARSER)
                .setDefault(".")
                .setRequired(false)
                .setGreedy(false);
        filepath.setHelp("Specify the path of the directory you wish to list.");
        jsap.registerParameter(filepath);

        Switch all = new Switch("All")
                        .setShortFlag('a')
                        .setLongFlag("all");
        all.setHelp("Use this switch to display all files, including hidden files and junctions or specify exactly which types of files you wish to display.");

        jsap.registerParameter(all);

        JSAPResult config = jsap.parse(args);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(config.getString("Path")))) {
            for (Path file: stream) {

                // Parse the file attributes into an object
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

                // Create some objects that will hold file attributes
                // Starting with last modification date:
                FileTime date = attr.lastModifiedTime();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String dateModified = df.format(date.toMillis());

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // Print it all out
                System.out.println(dateModified  + "        " + (isDir?"<DIR>":"     ") + "      " + file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }

    }
}
