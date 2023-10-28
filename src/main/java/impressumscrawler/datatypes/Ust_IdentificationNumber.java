package impressumscrawler.datatypes;


import org.jsoup.nodes.Element;

public class Ust_IdentificationNumber {
    private boolean indicator_in_same_element;

    private Element number_element;

    private int element_index;

    private String ust_number;

    public Ust_IdentificationNumber() {

    }

    public String report() {
        return ust_number;
    }

    public Ust_IdentificationNumber(boolean indicator, Element e, int index, String n) {
        this.indicator_in_same_element = indicator;
        this.number_element = e;
        this.element_index = index;
        this.ust_number = n;
    }

    public boolean isIndicator_in_same_element() {
        return indicator_in_same_element;
    }

    public void setIndicator_in_same_element(boolean indicator_in_same_element) {
        this.indicator_in_same_element = indicator_in_same_element;
    }

    public Element getNumber_element() {
        return number_element;
    }

    public void setNumber_element(Element number_element) {
        this.number_element = number_element;
    }

    public String getUst_number() {
        return ust_number;
    }

    public void setUst_number(String ust_number) {
        this.ust_number = ust_number;
    }
}
