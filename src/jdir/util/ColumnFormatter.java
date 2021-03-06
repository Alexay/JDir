package jdir.util;

/**
 * Used to format W and D's output in a column format
 *
 * USED BY: W.java, D.java
 */

import jdir.P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static jdir.Main.linePrintSetting;

public class ColumnFormatter {

    private List<List<String>> lines = new ArrayList<>();
    private List<Integer> maxLengths = new ArrayList<>();
    private int numColumns = -1;

    public ColumnFormatter addLine(String... line) {

        if (numColumns == -1){
            numColumns = line.length;
            for(int i = 0; i < numColumns; i++) {
                maxLengths.add(0);
            }
        }

        if (numColumns != line.length) {
            throw new IllegalArgumentException();
        }

        for(int i = 0; i < numColumns; i++) {
            maxLengths.set(  i, Math.max( maxLengths.get(i), line[i].length() )  );
        }

        lines.add( Arrays.asList(line) );

        return this;
    }

    public void print() {
        P.printIt(toString(), linePrintSetting);
    }

    public String toString(){
        String result = "";
        for(List<String> line : lines) {
            for(int i = 0; i < numColumns; i++) {
                result += pad( line.get(i), maxLengths.get(i) + 1);
            }
            result += System.lineSeparator();
        }
        return result;
    }

    private String pad(String word, int newLength){
        while (word.length() < newLength) {
            word += " ";
        }
        return word;
    }
}
