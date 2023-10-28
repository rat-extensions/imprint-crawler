package impressumscrawler.datatypes;

import org.apache.commons.collections.map.AbstractOrderedMapDecorator;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ChamberObject {

    private ArrayList<String> berufsbezeichnung;

    private Element element;
    private Elements elements;

    private boolean valid_chamber_found;

    private ArrayList<String> chamber_indicators;

    private ArrayList<String> valid_chambers;

    public ChamberObject(){

    }

    public String report() {
        String report = "";
        report += "Chamber indikatoren: \n";
        report += chamber_indicators.toString().replace("[", "").replace("]", "") + "\n";
        report += "Valide Chambers: \n";
        report += valid_chambers.toString().replace("[", "").replace("]", "") + "\n";
        report += "\n";

        return report;
    }

    public void report_debug() {
        System.out.println("Found indikators: " + chamber_indicators);
        System.out.println("Found valid_chambers: " + valid_chambers);
    }


    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public boolean isValid_chamber_found() {
        return valid_chamber_found;
    }

    public void setValid_chamber_found(boolean valid_chamber_found) {
        this.valid_chamber_found = valid_chamber_found;
    }

    public ArrayList<String> getChamber_indicatros() {
        return chamber_indicators;
    }

    public void setChamber_indicatros(ArrayList<String> chamber_indicatros) {
        this.chamber_indicators = chamber_indicatros;
    }

    public ArrayList<String> getValid_chambers() {
        return valid_chambers;
    }

    public void setValid_chambers(ArrayList<String> valid_chambers) {
        this.valid_chambers = valid_chambers;
    }

    public ArrayList<String> getBerufsbezeichnung() {
        return berufsbezeichnung;
    }

    public void setBerufsbezeichnung(ArrayList<String> berufsbezeichnung) {
        this.berufsbezeichnung = berufsbezeichnung;
    }

    public ArrayList<String> getChamber_indicators() {
        return chamber_indicators;
    }

    public void setChamber_indicators(ArrayList<String> chamber_indicators) {
        this.chamber_indicators = chamber_indicators;
    }
}
