package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class BerufsordnungsObject {

    private Elements elements;
    private Elements elements_with_regelung;
    private Elements elements_with_nennung;

    private ArrayList<String> regelung_links;


    public BerufsordnungsObject() {

    }

    public String report() {
        String report = "";
        for (String links : regelung_links) {
            report += links + "\n";
            report += "------- \n";
        }
        return report;
    }

    public ArrayList<String> getRegelung_links() {
        return regelung_links;
    }

    public void setRegelung_links(ArrayList<String> regelung_links) {
        this.regelung_links = regelung_links;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public Elements getElements_with_regelung() {
        return elements_with_regelung;
    }

    public void setElements_with_regelung(Elements elements_with_regelung) {
        this.elements_with_regelung = elements_with_regelung;
    }

    public Elements getElements_with_nennung() {
        return elements_with_nennung;
    }

    public void setElements_with_nennung(Elements elements_with_nennung) {
        this.elements_with_nennung = elements_with_nennung;
    }
}
