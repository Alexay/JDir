package jdir;

import java.util.Scanner;

public class P {

    static int linesPrinted = 0;

    public static void printIt(String s, int lines) {
        if (lines == -10)
            System.out.println(s);
        else if (lines-1 <= linesPrinted) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Press ENTER to continue...");
            scan.nextLine();
            linesPrinted = 0;
            System.out.println(s);
            linesPrinted++;
        } else {
            System.out.println(s);
            linesPrinted++;
        }
    }
}