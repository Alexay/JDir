package jdir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

/**
 *   Use this class to read the owner of a file.
 *
 *   USED BY: StandardDisplay.java
 */
public class FileOwnerReader {

    public static String read(Path pathToReadForOwnerAttributes) throws IOException {
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(pathToReadForOwnerAttributes, FileOwnerAttributeView.class);
        UserPrincipal owner = ownerAttributeView.getOwner();
        return owner.getName();
    }

}