package impressumscrawler.search_component;

public class ImprintFinder_handler {

    public static void main(String[] args) {
        String[] links = {"www.aldi-sued.de", "www.buegelservicelandau.de.rs", "www.copyshop-duisburg.de", "holzblog.schule", "www.igus.de", "www.kuketz-blog.de", "www.thyssenkrupp.com", "www.uni-due.de", "www.wurm-holz.de", "www.thiemer.com", "kleintierpraxis-fuhrmann.de", "www.arkaden-apotheke.de", "www.praxis-am-brunnen.de", "www.zieglerundkollegen.de", "www.pkf-fasselt.de"};

        ImprintFinder imprintFinder = new ImprintFinder();

            for (String link : links) {
                imprintFinder.search_imprint(link);
            }
    }

}
