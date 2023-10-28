package data.audiovisuelle_behoerden;

import data.audiovisuelle_behoerden.Behoerden;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
/**
 * Class implemented after the singleton pattern. Provides a HashSet with all chambers
 */
public class Behoerden {
    private static final Behoerden behoerden = new Behoerden();

    private HashSet<String> behoerden_hashset;

    public HashSet<String> getBehoerden_hashset() {
        return behoerden_hashset;
    }

    public boolean containsBehoerde(String s ) {
        return behoerden_hashset.contains(s);
    }

    private Behoerden() {
        behoerden_hashset = new HashSet<String>();
        try {
            File file = new File("src/main/java/data/audiovisuelle_behoerden/audio_visuellebehoerden.txt");
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String name = scanner.nextLine();
                behoerden_hashset.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Behoerden getInstance() {
        return behoerden;
    }
}
