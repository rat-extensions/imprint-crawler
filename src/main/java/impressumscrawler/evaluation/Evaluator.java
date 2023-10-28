package impressumscrawler.evaluation;

import impressumscrawler.Impressumscrawler;
import impressumscrawler.datatypes.EditorInfoObject;
import impressumscrawler.datatypes.ImprintCrawlObject;
import impressumscrawler.extraction_component.FieldExtraction.FieldExtractor;
import impressumscrawler.util.Util;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.lang.model.util.Elements;
import java.util.ArrayList;

public class Evaluator {

    /**
     * Reads link from file found in "data/evaluation/links". Each line is interpreted as a link.
     * With the help of the report() function of the ImprintSearchObject and the ImprintObject Object
     * the results are written into a file inside of "data/evaluation/imprintObjects/".
     */
    private static void evaluateImpressumscrawler() {
        Impressumscrawler impressumscrawler = new Impressumscrawler();
        ArrayList<String> links = Util.getArrayListFromFile("data/evaluation/links");
        ArrayList<ImprintCrawlObject> imprintCrawlObjects = new ArrayList<>();
        links.forEach(link -> imprintCrawlObjects.add(impressumscrawler.crawlPage(link)));

        for (ImprintCrawlObject ic : imprintCrawlObjects) {
            String search_report = ic.getImprintSearchObject().report();
            String extraction_report = ic.getImprintObject().report();

            Util.writeStringToPath("data/evaluation/imprintObjects/"+ic.getImprintSearchObject().getHomepage_url().replaceAll("[/\\\\]", ""),search_report + "\n" + extraction_report);
        }
    }

    /**
     * Created to take a look into the homepages that had mistakes, inside of the evaluation, when it comes to the editors
     */
    private static void evaluate_editors() {
        Impressumscrawler impressumscrawler = new Impressumscrawler();
        FieldExtractor fieldExtractor = new FieldExtractor(null);
        ArrayList<String> links = Util.getArrayListFromFile("data/evaluation/editor_information");
        ArrayList<ImprintCrawlObject> imprintCrawlObjects = new ArrayList<>();
        links.forEach(link -> imprintCrawlObjects.add(impressumscrawler.crawlPage(link)));
        ArrayList<EditorInfoObject> eio = new ArrayList<>();
        for (ImprintCrawlObject ico :imprintCrawlObjects) {
            Document doc = ico.getImprintSearchObject().getImprint_doc();
            fieldExtractor.setCurrent_document(doc);
            eio = fieldExtractor.findEditorInfo();
        }
        for (EditorInfoObject ei : eio ) {
            System.out.println("------------------");
            ei.report();
        }
    }


    /**
     * Conducts the different designed evaluations
     * @param args
     */
    public static void main(String[] args) {
       evaluateImpressumscrawler();
        //evaluate_editors();





    }
}
