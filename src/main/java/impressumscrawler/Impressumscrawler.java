package impressumscrawler;


import impressumscrawler.datatypes.ImprintCrawlObject;
import impressumscrawler.datatypes.ImprintObject;
import impressumscrawler.datatypes.ImprintSearchObject;
import impressumscrawler.extraction_component.FieldExtraction.FieldExtractor;
import impressumscrawler.extraction_component.ImprintExtraction.ImprintExtractor;
import impressumscrawler.search_component.ImprintFinder;
import impressumscrawler.util.Util;
import org.jsoup.nodes.Document;

public class Impressumscrawler {
    /**
     *
     * Dirigates the crawling attempt.
     * Disptaches url to Imprint finder,
     * gives the Document to an Imprint Extractor
     * Both classes report their results, and meta data
     * in their corresponding objects.
     * When all is done. The results are brought back in
     * a specific data format. That can be specified.
     *
     */

    private ImprintExtractor imprintExtractor;
    private ImprintFinder imprintFinder;

    /**
     * Constructor, which automatically initializes an ImprintExtractor and ImprintFinderObject.
     */
    public Impressumscrawler() {
        this.imprintExtractor = new ImprintExtractor();
        this.imprintFinder = new ImprintFinder();
    }

    /**
     * Forwards the url, that shall be parsed, to the imprintFinder, which returns an ImprintSearchObject
     * If a valid document is returned, the imprintExtractor is advised to analyze the document.
     * After the Extractor returns an Imprint, an ImprintCrawlObject is created and returned.
     * @param   url                 that gets analyzed
     * @return  ImprintCrawlObject  which holds the ImprintSearchObject and ImprintObject of the analysis.
     */
    public ImprintCrawlObject crawlPage(String url) {
        ImprintCrawlObject imprintCrawlObject = new ImprintCrawlObject();
        System.out.println("Crawling: " + url);
        //start imprintFinder
        ImprintSearchObject imprintSearchObject = imprintFinder.search_imprint(url);

        //set imprintExtractor and analyze Doc
        Document imprint_document = imprintSearchObject.getImprint_doc();
        ImprintObject imprintObject = imprintExtractor.analyzeDoc(imprint_document);

        //set imprintCrawlObject
        imprintCrawlObject.setImprintSearchObject(imprintSearchObject);
        imprintCrawlObject.setImprintObject(imprintObject);

        return imprintCrawlObject;
    }

    /**
     * In case that a search for a document isn't needed we can also just call the impressumcrawler on a specific
     * Document. In that case the crawler just extracts the contents of the documents without conducting a search.
     * @param doc Document that should be parsed for the imprint fields
     * @return Imprintcrawlobject of the crawled Document
     */
    public ImprintCrawlObject crawlPage(Document doc) {
        ImprintCrawlObject imprintCrawlObject = new ImprintCrawlObject();
        Document imprint_document = doc;
        ImprintObject imprintObject = imprintExtractor.analyzeDoc(imprint_document);
        //set imprintCrawlObject
        imprintCrawlObject.setImprintSearchObject(null);
        imprintCrawlObject.setImprintObject(imprintObject);

        return imprintCrawlObject;

    }



}
