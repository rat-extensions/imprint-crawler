package impressumscrawler.datatypes;



import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class VertretungsberechtigterObject {
    private Elements elements;

    private Element role_element;
    private int role_element_index;

    private Element name_element;
    private int name_element_index;

    private ArrayList<String> vertretungs_indikatoren;
    private ArrayList<String> found_names;

    public VertretungsberechtigterObject () {
    }

    public String report() {
        String report = "";
        report += "Vertreter indikatoren: ";
        report += vertretungs_indikatoren.toString().replace("[", "").replace("]", "") + "\n";
        report += "Gefundene Vertreter: ";
        report += found_names.toString().replace("[", "").replace("]", "") + "\n";
        return report;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
        //super.setElements(elements);
    }

    public Element getRole_element() {
        return role_element;
    }

    public void setRole_element(Element role_element) {
        this.role_element = role_element;
    }

    public int getRole_element_index() {
        return role_element_index;
    }

    public void setRole_element_index(int role_element_index) {
        this.role_element_index = role_element_index;
    }

    public Element getName_element() {
        return name_element;
    }

    public void setName_element(Element name_element) {
        this.name_element = name_element;
    }

    public int getName_element_index() {
        return name_element_index;
    }

    public void setName_element_index(int name_element_index) {
        this.name_element_index = name_element_index;
    }

    public ArrayList<String> getVertretungs_indikatoren() {
        return vertretungs_indikatoren;
    }

    public void setVertretungs_indikatoren(ArrayList<String> vertretungs_indikatoren) {
        this.vertretungs_indikatoren = vertretungs_indikatoren;
    }

    public ArrayList<String> getFound_names() {
        return found_names;
    }

    public void setFound_names(ArrayList<String> found_names) {
        this.found_names = found_names;
    }


}
