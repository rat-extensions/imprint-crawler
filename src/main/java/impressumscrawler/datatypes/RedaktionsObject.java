package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RedaktionsObject {

    private Elements elements;
    //private Element element;
    private String indikator;

    private Elements elements_with_indikator;

    public RedaktionsObject() {

    }

    public String report() {
        return indikator;
    }



    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public String getIndikator() {
        return indikator;
    }

    public void setIndikator(String indikator) {
        this.indikator = indikator;
    }

    public Elements getElements_with_indikator() {
        return elements_with_indikator;
    }

    public void setElements_with_indikator(Elements elements_with_indikator) {
        this.elements_with_indikator = elements_with_indikator;
    }


    //private int element_index;

}
