package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RechtsformObject {
    private Elements elements;
    private Element element;
    private ArrayList<String> found_rechtsform_indicators;

    public RechtsformObject() {

    }

    public String report() {
        return found_rechtsform_indicators.toString() + "\n";
        /*for(String ffi : found_firm_indicators) {
            report += ffi + "\n";
        } */
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public ArrayList<String> getFound_rechtsform_indicators() {
        return found_rechtsform_indicators;
    }

    public void setFound_rechtsform_indicators(ArrayList<String> found_rechtsform_indicators) {
        this.found_rechtsform_indicators = found_rechtsform_indicators;
    }
}
