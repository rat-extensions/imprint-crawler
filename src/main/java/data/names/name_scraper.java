package data.names;

import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class name_scraper {
    /**
     * Scrapes 10000 Names from Wikipedia and saves it in a file.
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
            Document doc = null;

            try {
                doc = Jsoup.connect("https://de.wikipedia.org/wiki/Liste_von_Vornamen").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = doc.select(".center");
            System.out.println(elements);
            Elements links = elements.select("a[href]");
            ArrayList<String> links_as_strings = new ArrayList<String>();
            for (Element link : links) {
                links_as_strings.add(link.attr("href"));
            }
            System.out.println(links_as_strings);

            ArrayList<Document> name_docs = new ArrayList<Document>();

            for (String link : links_as_strings) {
                try {
                    Document name_doc = Jsoup.connect("https://de.wikipedia.org" + link).get();
                    name_docs.add(name_doc);
                    //TimeUnit.SECONDS.sleep(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Show Elements...");

            ArrayList<String> names = new ArrayList<>();

            for(Document name_doc : name_docs) {
                Elements all_elements = name_doc.getAllElements();

                Elements name_elements = all_elements.select("h2 + p"); // selects h2 that has p as sibling see "https://jsoup.org/cookbook/extracting-data/selector-syntax" for more info

                for(Element e :name_elements) {
                    for (Element ec : e.children()) {
                        //System.out.println("A name: " + ec.ownText());
                        names.add(ec.ownText());
                    }
                }
            }

            // Write names into file
            try {
                FileWriter writer = new FileWriter("src/main/java/data/names/names.txt");
                for (String str : names) {
                    writer.write(str + System.lineSeparator());
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
