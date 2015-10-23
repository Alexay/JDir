package com.alex;

import joptsimple.OptionSet;

import java.nio.file.Path;

/**
 * This is the wide output mode, designed to output bare-like filenames in a Unix-like fashion of multiple columns.
 *
 *
 * USED BY: Main.java
 */


public class W {
    public static void display(Path[] filesForDisplay, OptionSet options) {

        for (Path aPath : filesForDisplay) {
            System.out.println((options.has("l") ? aPath.getFileName().toString().toLowerCase() : aPath.getFileName()));
        }
    }
}
