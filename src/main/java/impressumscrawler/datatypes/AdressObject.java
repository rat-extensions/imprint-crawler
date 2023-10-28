package impressumscrawler.datatypes;

import javax.print.Doc;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AdressObject {

    private Elements elements;

    private Element plz_element;
    private Element street_element;
    private Element adressed_element;

    private String adressed;
    private String street;
    private String plz_location;

    private boolean street_indication;
    private boolean firm_indication;

    private boolean is_valid_address;
    private boolean is_postal_address;

    private String street_indicator;
    private String firm_indicator;

    private int plz_element_index;
    private int street_element_index;
    private int name_element_index;

    private int proximity_indentifier; //deprecated

    public AdressObject(){

    }

    private void m(String info, String value) {
        System.out.println(info + value);
    }

    public boolean getIs_postal_address() {
        return is_postal_address;
    }

    public void setIs_postal_address(boolean is_postal_address) {
        this.is_postal_address = is_postal_address;
    }

    public boolean getIs_valid_address() {
        return is_valid_address;
    }

    public void setIs_valid_address(boolean is_valid_address) {
        this.is_valid_address = is_valid_address;
    }

    public void report_indezes() {
        m("Adressed Index:      ", String.valueOf(this.name_element_index));
        m("Street Index:        ", String.valueOf(this.street_element_index));
        m("PLZ + loc index:     ", String.valueOf(this.plz_element_index));
    }

    public void report_adress_and_indezes() {
        m("Adress:        " , this.adressed + " Index: " + String.valueOf(this.name_element_index));
        m("Street:        " , this.street + " Index: "+ String.valueOf(this.street_element_index));
        m("PLZ + loc:     " , this.plz_location + " Index: " + String.valueOf(this.plz_element_index));
        //m("index delta: " , standard this.name_element_index)
    }

    public void report_adress_and_indezes_indicators() {
        m("Adress:        " , this.adressed +" Indicator found:"+ this.firm_indication + " Indicator: " + this.firm_indicator +" Index: " + String.valueOf(this.name_element_index));
        m("Street:        " , this.street + " Indicator found: "+ this.street_indication + " Indicator: " + this.street_indicator + " Index: "+ String.valueOf(this.street_element_index));
        m("PLZ + loc:     " , this.plz_location + " Index: " + String.valueOf(this.plz_element_index));
        //m("index delta: " , standard this.name_element_index)
    }

    public void report_adress_with_indicator() {
        m("isFirm/indicator ", this.firm_indication + "/"+ this.firm_indicator);
        m("isStreet/indicator ", this.street_indication +"/"+this.street_indicator);
        report_adress();
    }

    public void report_adress() {
        m("", this.adressed);
        m("", this.street);
        m("", this.plz_location);
    }

    public String report() {
        String report = "";
        report += this.adressed + "\n";
        report += this.street + "\n";
        report += this.plz_location + "\n";
        return report;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public String getStreet_indicator() {
        return street_indicator;
    }

    public void setStreet_indicator(String street_indicator) {
        this.street_indicator = street_indicator;
    }

    public String getFirm_indicator() {
        return firm_indicator;
    }

    public void setFirm_indicator(String firm_indicator) {
        this.firm_indicator = firm_indicator;
    }

    public Element getPlz_element() {
        return plz_element;
    }

    public void setPlz_element(Element plz_element) {
        this.plz_element = plz_element;
    }

    public Element getStreet_element() {
        return street_element;
    }

    public void setStreet_element(Element street_element) {
        this.street_element = street_element;
    }

    public Element getAdressed_element() {
        return adressed_element;
    }

    public void setAdressed_element(Element adressed_element) {
        this.adressed_element = adressed_element;
    }

    public String getAdressed() {
        return adressed;
    }

    public void setAdressed(String adressed) {
        this.adressed = adressed;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPlz_location() {
        return plz_location;
    }

    public void setPlz_location(String plz_location) {
        this.plz_location = plz_location;
    }

    public boolean isStreet_indication() {
        return street_indication;
    }

    public void setStreet_indication(boolean street_indication) {
        this.street_indication = street_indication;
    }

    public boolean isFirm_indication() {
        return firm_indication;
    }

    public void setFirm_indication(boolean firm_indication) {
        this.firm_indication = firm_indication;
    }

    public int getPlz_element_index() {
        return plz_element_index;
    }

    public void setPlz_element_index(int plz_element_index) {
        this.plz_element_index = plz_element_index;
    }

    public int getStreet_element_index() {
        return street_element_index;
    }

    public void setStreet_element_index(int street_element_index) {
        this.street_element_index = street_element_index;
    }

    public int getName_element_index() {
        return name_element_index;
    }

    public void setName_element_index(int name_element_index) {
        this.name_element_index = name_element_index;
    }

    public int getProximity_indentifier() {
        return proximity_indentifier;
    }

    public void setProximity_indentifier(int proximity_indentifier) {
        this.proximity_indentifier = proximity_indentifier;
    }
}
