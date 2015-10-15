package szu.alex;

import joptsimple.OptionSet;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 *  This is the "Bare" display mode, which outputs only the names of the files.
 *
 *  USED BY: Main.java
 */
public class B {

    public static void display(Path[] filesForDisplay, OptionSet options) {
        for (Path aPath : filesForDisplay) {
            System.out.println((options.has("l") ? aPath.getFileName().toString().toLowerCase() : aPath.getFileName()));
        }
    }
}