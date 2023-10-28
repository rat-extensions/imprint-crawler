package data.kammern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Kammer_scraper {
    //Infos für die Kammern: https://www.kammerrecht.de/deutschland/

    /**
     * Scrapes the different chambers from the different responsible sites.
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ArrayList<String> chambers = new ArrayList<String>();


        String apotheker_link = "https://www.abda.de/ueber-uns/bak/";
        String apotheker_css_selector = "span.c-accordion__trigger-title";

        chambers.addAll(extract_chambers(apotheker_link, apotheker_css_selector));

        String aerzte_link = "https://www.bundesaerztekammer.de/presse/kontakt/pressestellen-der-landesaerztekammern";
        String aezte_css_selector = "div.ce-bodytext > h2";

        chambers.addAll(extract_chambers(aerzte_link, aezte_css_selector));

        String tieraerzte_link = "https://www.bundestieraerztekammer.de/btk/mitglieder/";
        String tieraerzte_css_selector = "address > b";

        chambers.addAll(extract_chambers(tieraerzte_link, tieraerzte_css_selector));

        String psychotherapeuten = "https://www.ptk-nrw.de/kammer/links";
        String psychotherapeuten_css_selector = "div#c1027 > div > div > div > div > ul > li";

        ArrayList<String> psychotherapeuten_chambers = extract_chambers(psychotherapeuten, psychotherapeuten_css_selector);
        psychotherapeuten_chambers.forEach(token -> {
            int link_start_index = token.indexOf("www");
            //System.out.println(link_start_index);
            String chamber_cleaned = token.substring(0, link_start_index).trim();
            //return token.substring(0, link_start_index);
            chambers.add(chamber_cleaned);
        } );

        String zahnaerzte_link = "https://www.bzaek.de/ueber-uns/organisationsstruktur/zahnaerztekammern-der-laender.html";
        String zahnaerzte_css_selector = "h3 > span.publication-teaser__headline";

        chambers.addAll(extract_chambers(zahnaerzte_link, zahnaerzte_css_selector));

        String bundesnotarkammer_link = "https://www.bnotk.de/die-bundesnotarkammer/mitglieder";
        String bundesnotarkammer_css_selector = "div.district-info > ul > li > h2";

        chambers.addAll(extract_chambers(bundesnotarkammer_link, bundesnotarkammer_css_selector));

        String anwaelte_link = "https://www.brak.de/die-brak/rechtsanwaltskammern/";
        String anwaelte_css_selector = "section#c7413 > div > div > div > p > strong";

        chambers.addAll(extract_chambers(anwaelte_link, anwaelte_css_selector));

        String steuerberater_link = "https://www.bstbk.de/de/ueber-uns/steuerberaterkammern";
        String steuerberater_css_selector = "div.infos > h2.red.none-typocase";

        chambers.addAll(extract_chambers(steuerberater_link,steuerberater_css_selector));

        String patentanwaelte = "Patentanwaltskammer";

        chambers.add(patentanwaelte);

        String wirtschaftspruefer = "Wirtschaftsprüferkammer";

        chambers.add(wirtschaftspruefer);

        String architekten_link = "https://bak.de/kammer-und-beruf/mitglieder/";
        String architekten_css_selector = "h3.teaser__heading";

        chambers.addAll(extract_chambers(architekten_link, architekten_css_selector));

        String ingenieurkammer_link = "https://bingk.de/ueber-uns/mitglieder/";
        String ingenieurkammer_css_selector = "h5 > strong";

        chambers.addAll(extract_chambers(ingenieurkammer_link, ingenieurkammer_css_selector));

        String lotsen_link = "https://www.bundeslotsenkammer.de/infothek/wichtige-links/";
        String lotsen_css_selector = "p:has(b:contains(LOTSENBRÜDERSCHAFTEN:)) ~ p:has(strong)";
        ArrayList<String> lotsen_chambers = extract_chambers(lotsen_link, lotsen_css_selector);

        lotsen_chambers.forEach(token -> {
            int link_start_index = token.indexOf("http");
            String chamber_cleaned = token.substring(0, link_start_index).trim();
            chambers.add("Lotsenbrüderschaft " + chamber_cleaned);
        } );

        System.out.println(chambers);

        // Write chambers into file
        try {
            FileWriter writer = new FileWriter("src/main/java/data/kammern/kammern.txt");
            for (String str : chambers) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> extract_chambers(String link, String css_selector) {
        ArrayList<String> chamber_list = new ArrayList<String>();
        Document local_doc = null;
        try {
            local_doc = Jsoup.connect(link).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements selected_elements = local_doc.select(css_selector);
        for (Element e : selected_elements) {
            chamber_list.add(e.text());
        }
        System.out.println("gefundene Kammern: " +chamber_list);
        System.out.println("Anzahl der Kammern: " + chamber_list.size());
        return chamber_list;
    }

}
