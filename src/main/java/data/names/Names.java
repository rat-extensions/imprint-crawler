package data.names;

import impressumscrawler.ner.Pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Class implemented after the singleton pattern. Provides a HashSet with all chambers
 */
public class Names {

    private static final Names names = new Names();

    private HashSet<String> names_hashset;

    public HashSet<String> getNames_hashset() {
        return names_hashset;
    }

    public boolean containsName(String s ) {
        return names_hashset.contains(s);
    }

    private Names() {
        names_hashset = new HashSet<String>();
        try {
            File file = new File("src/main/java/data/names/names.txt");
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String name = scanner.nextLine();
                names_hashset.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Names getInstance() {
        return names;
    }
}
