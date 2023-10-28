package data.kammern;

import data.kammern.Kammern;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Class implemented after the singleton pattern. Provides a HashSet with all chambers
 */
public class Kammern {
    private static final Kammern kammern = new Kammern();

    private HashSet<String> kammern_hashset;

    public HashSet<String> getKammern_hashset() {
        return kammern_hashset;
    }

    public boolean containsKammer(String s ) {
        return kammern_hashset.contains(s);
    }


    private Kammern() {
        kammern_hashset = new HashSet<String>();
        try {
            File file = new File("src/main/java/data/kammern/kammern.txt");
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String name = scanner.nextLine();
                kammern_hashset.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Kammern getInstance() {
        return kammern;
    }
}
