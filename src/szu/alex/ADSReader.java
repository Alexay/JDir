package szu.alex;

/**
 * Use this class to parse NTFS's Alternate Data Stream in Windows directories
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ADSReader {
    public static void main(String[] args) {
        new ADSReader().start();
    }

    private void start() {
        File file = new File("test.txt:hidden");
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            String hidden = bf.readLine();
            System.out.println(hidden);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}