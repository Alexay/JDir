package com.alex;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *  This class is used to enable thousand separator in file sizes .
 *
 *  USED BY: StandardDisplay.java
 */

public class C {
    public static String thousandSeparate(long fileSize) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        String output = formatter.format(fileSize);
        return output;
    }
}