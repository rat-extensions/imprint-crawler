package data.audiovisuelle_behoerden;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class implemented after the singleton pattern. Provides a HashSet with all chambers
 */
public class Behoerden_scraper {
    public static void main(String[] args) {
        Document doc = null;

        try {
            doc = Jsoup.connect("https://www.getlaw.de/blog/informationspflichten-fuer-audiovisuelle-mediendiensteanbieter/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements names_of_behoerden = doc.select("div.ce_text.block>p>a");
        ArrayList<String> behoerden_namen = new ArrayList<>();
        for (Element e : names_of_behoerden) {
            String behoerde_mit_short = e.ownText();
            String behoerde_ohne_short = behoerde_mit_short.replaceAll("\\(.*\\)", "").trim();
            // behoerden_namen.add(behoerde_mit_short);
            behoerden_namen.add(behoerde_ohne_short);

        }

        // Write chambers into file
        try {
            FileWriter writer = new FileWriter("src/main/java/data/audiovisuelle_behoerden/audio_visuelle_behoerden.txt");
            for (String str : behoerden_namen) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
