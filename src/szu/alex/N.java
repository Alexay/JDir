package szu.alex;

import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;

/**
 * This is the display mode for the MS-DOS/Win95 style 8.3 filenames with their names on the left.
 *
 * USED BY: Main.java
 */


public class N {
    public static String getMSDOSName(String fileName)
            throws IOException, InterruptedException {

        String path = getAbsolutePath(fileName);

        Process process =
                Runtime.getRuntime().exec(
                        "cmd /c for %I in (\"" + path + "\") do @echo %~snI");

        process.waitFor();

        byte[] data = new byte[65536];
        int size = process.getInputStream().read(data);

        if (size <= 0)
            return null;

        return new String(data, 0, size).replaceAll("\\r\\n", "");
    }

    public static String getAbsolutePath(String fileName)
            throws IOException {
        File file = new File(fileName);
        String path = file.getAbsolutePath();

        if (file.exists() == false)
            file = new File(path);

        path = file.getCanonicalPath();

        if (file.isDirectory() && (path.endsWith(File.separator) == false))
            path += File.separator;

        return path;
    }


    public static void display(Path[] filesForDisplay, OptionSet options) {

        int dirCounter = 0;

        try {
            for (Path aPath : filesForDisplay) {
                // First, we'll initialize the different variables that may occur in the filtering
                DosFileAttributes attr = Files.readAttributes(aPath, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

                // Let's get our timestamp depending on the option given by the user.
                String timeStamp = T.parseTime(attr, options);

                // Is it a directory?
                boolean isDir = attr.isDirectory();

                // If the path points to a directory, we also increment our directory counter for the output footer.
                if (isDir)
                    dirCounter++;

                // Is it reparse point / junction?
                boolean isJunction = ReparsePointAttributeReader.read(attr);


                // We initialize the fileName for the conditional steps.
                String fileName = getMSDOSName(aPath.getFileName().toString());

                // If the user used the "l" option, we convert the file name to lowercase characters.
                if (options.has("l"))
                    fileName = fileName.toLowerCase();

                // OK, we've initialized everything we need, let's print!
                System.out.println(
                        fileName + "    " +
                                (isDir ?
                                        (isJunction ?
                                                "<JUNCTION>  " : "<DIR>       ") : "          ") +
                                (options.has("c") ?
                                        C.thousandSeparate(attr.size()):attr.size()) + " " +
                                timeStamp
                );
            }
        } catch (IOException b) {
            b.printStackTrace();
        } catch (InterruptedException c) {}

        // Here we initializer the directory
        int nonDirCounter = filesForDisplay.length-dirCounter;
        System.out.println(dirCounter);
        System.out.println(nonDirCounter);

    }
}
