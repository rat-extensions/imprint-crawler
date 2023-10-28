package impressumscrawler.search_component;

import com.sun.prism.null3d.NULL3DPipeline;
import impressumscrawler.datatypes.ImprintLocation;
import impressumscrawler.datatypes.ImprintSearchObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ImprintFinder {



    public ImprintFinder() {
    }

    ImprintSearchObject imprintSearchObject;

    /**
     * Takes URL request the website and searches for an Imprint.
     * @param homepage_url the Url that the search should be conducted on
     * @return Returns and ImprintSearchObject that contains a possible Imprint-Document and also some information about the search itself.
     * @throws NullPointerException when the url couldn't be requested
     */
    public ImprintSearchObject search_imprint(String homepage_url) throws NullPointerException {
        imprintSearchObject = new ImprintSearchObject();
        Document homepage = request("http://" +homepage_url);
        imprintSearchObject.setHomepage_url(homepage_url);
        imprintSearchObject.setImprint_doc(homepage);
        imprintSearchObject.setImprint_link(homepage_url);
        imprintSearchObject.setImprintLocation(ImprintLocation.HOMEPAGE_INSECURE);

        Document imprint;
        if(homepage != null) {
            boolean imprint_found = false;
            //See if we have a concrete impressum link
            for (Element link : homepage.select("a[href]")) {
                String current_link = link.absUrl("href");
                //System.out.println("------------------------------ ");

                if (current_link.contains("impressum") || current_link.contains("imprint")) {
                    this.imprintSearchObject.setImprintLocation(ImprintLocation.IMPRINT);
                    System.out.println("Imprint URL Found");
                    imprint = request(current_link);
                    this.imprintSearchObject.setImprint_doc(imprint);
                    this.imprintSearchObject.setImprint_link(current_link);
                    imprint_found = true;
                }
            }
            //See if we have a contact link and if the page behind the link, has an impressum indicator
            if(!imprint_found) {
                for (Element link : homepage.select("a[href]")) {
                    String current_link = link.absUrl("href");
                    //System.out.println("------------------------------ ");

                    if (current_link.contains("kontakt") || current_link.contains("contact")) {
                        this.imprintSearchObject.setImprintLocation(ImprintLocation.KONTAKT_INSECURE);
                        System.out.println("Imprint maybe in contacts");
                        imprint = request(current_link);
                        for (Element e : imprint.getAllElements()) {
                            if (e.ownText().contains("impressum") || e.ownText().contains("imprint")) {
                                this.imprintSearchObject.setImprintLocation(ImprintLocation.KONTAKT);
                                imprint_found = true;
                            }
                        }
                        this.imprintSearchObject.setImprint_doc(imprint);
                        this.imprintSearchObject.setImprint_link(current_link);
                    }
                }
                //Check if on the homepage is some info. If yes, lets set the homepage as doc. If it is not leave what ever status we have.
                for (Element element : homepage.getAllElements()) {
                    //String current_link = link.absUrl("href");
                    //System.out.println("------------------------------ ");
                    if (element.ownText().contains("kontakt") || element.ownText().contains("contact") || element.ownText().contains("Impressum") || element.ownText().contains("imprint")) {
                        this.imprintSearchObject.setImprint_doc(homepage);
                        this.imprintSearchObject.setImprintLocation(ImprintLocation.HOMEPAGE);
                        this.imprintSearchObject.setImprint_link(homepage_url);
                    }

                }
            }
        } else {
            throw new NullPointerException();
        }
        return imprintSearchObject;
    }

    /**
     * Requests the Document of a given url
     * @param url of the document that needs to be requested
     * @return retunrs a Jsoup.Document
     */
    private Document request(String url)  {
        try{
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if(con.response().statusCode() == 200) {
                System.out.println("Link: "+url);
                System.out.println("Title: " + doc.title());
                return doc;
            } else {
                System.out.println(url + " Bad Statuscode: " + con.response().statusCode());
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
