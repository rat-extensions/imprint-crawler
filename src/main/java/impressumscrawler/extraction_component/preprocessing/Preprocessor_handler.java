package impressumscrawler.extraction_component.preprocessing;

import impressumscrawler.extraction_component.FieldExtraction.FieldExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class Preprocessor_handler {
    public static void main(String[] args) {
        Document doc = null;
        try {
            File input = new File(System.getProperty("user.dir") + "/src/data/xml/XML_concrete/imprints_source/copyshop-duisburg.de/copyshop-duisburg.de.html");
            doc = Jsoup.parse(input, "UTF-8", "http://copzshop-duisburg.de/");

        } catch (IOException e) {
            e.printStackTrace();
        }

        ;

        Preprocessor processor = new Preprocessor(doc);
        //Elements processed_doc = processor.text_blocks();
        Document document = processor.preprocess_document(doc);
        System.out.println(document);
        //System.out.println(processed_doc);
        //FieldExtractor fieldExtractor = new FieldExtractor(doc);

    }
}
