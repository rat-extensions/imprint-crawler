package impressumscrawler.extraction_component.preprocessing;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.helper.ValidationException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {
    //Beautiful soup document
    private Document current_document;

    public Preprocessor(Document doc) {
        current_document = doc;
    }

    private static String[] non_structural_tags_array = {"span", "stronger"};

    public static Document preprocess_document(Document doc) {
        //System.out.println(doc);
        String doc_as_string = doc.toString();
        for (String tag : non_structural_tags_array) {
            String search_pattern = ("(<"+tag+"[^>]*>|<\\/"+tag+"[^>]*>)");
            Pattern pattern = Pattern.compile(search_pattern);
            Matcher matcher = pattern.matcher(doc_as_string);

            while (matcher.find()) {
                System.out.println(matcher.group());
            }


           doc_as_string = doc_as_string.replaceAll(search_pattern, "");
        }
        return Jsoup.parse(doc_as_string);
    }

    public Elements text_blocks() {

        List<String> non_structural_tags = Arrays.asList(non_structural_tags_array);

        Elements elements = current_document.getAllElements();
        Elements elements_no_structure = new Elements();

        for (Element e : elements) {
            Element added_element = null;
            String e_tag_name = e.tagName();
            if(non_structural_tags.contains(e_tag_name)) {
                try {
                    added_element = new Element("<>").append(e.html());
                } catch (ValidationException exception) {
                    System.out.println("ValidationException");
                    System.out.println(e);
                    added_element = e;
                }
            } else {
                added_element = e;
            }
            elements_no_structure.add(added_element);
        }
        //System.out.println("Count: " + count);
        return elements_no_structure;
    }
}
