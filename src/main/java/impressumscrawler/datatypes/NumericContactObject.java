package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NumericContactObject {
    Elements elements;
    Element element;

    String contact_info;
    String contact_type;

    public NumericContactObject() {

    }

    public String report() {
        String report = "";
        report += contact_type + " " + contact_info + "\n";
        return report;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_indicator) {
        this.contact_type = contact_indicator;
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

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

}
