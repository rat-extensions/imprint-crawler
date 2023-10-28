package impressumscrawler.datatypes;

import org.jsoup.nodes.Document;

public class ImprintSearchObject {

    private String homepage_url;
    private String imprint_link;
    private Document imprint_doc;
    private ImprintLocation imprintLocation;

    public ImprintSearchObject() {

    }

    public String report() {
        String report = "" +
                "Homepage_Url: " + homepage_url + "\n" +
                "imprint Link: " + imprint_link + "\n" +
                "imprint Location: " + imprintLocation.name().toString() + "\n" +
                "\n";
        return report;
    }

    public String getHomepage_url() {
        return homepage_url;
    }

    public void setHomepage_url(String homepage_url) {
        this.homepage_url = homepage_url;
    }

    public String getImprint_link() {
        return imprint_link;
    }

    public void setImprint_link(String imprint_link) {
        this.imprint_link = imprint_link;
    }

    public Document getImprint_doc() {
        return imprint_doc;
    }

    public void setImprint_doc(Document imprint_doc) {
        this.imprint_doc = imprint_doc;
    }

    public ImprintLocation getImprintLocation() {
        return imprintLocation;
    }

    public void setImprintLocation(ImprintLocation imprintLocation) {
        this.imprintLocation = imprintLocation;
    }
}
