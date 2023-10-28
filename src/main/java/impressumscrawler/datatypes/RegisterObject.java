package impressumscrawler.datatypes;


import org.jsoup.nodes.Element;

public class RegisterObject {
    private String register_number;
    private Element register_number_element;
    private int register_number_element_index;

    private String register_court;
    private Element register_court_element;
    private int register_court_element_index;

    public RegisterObject() {

    }

    public String report() {
        String report = "";
        report += register_court + " " + register_number;
        return report;
    }

    public void reportRegisterCourtAndNumber() {
        System.out.println("Court: " + this.getRegister_court());
        System.out.println("Number: " + this.getRegister_number());
    }

    public String getRegister_number() {
        return register_number;
    }

    public void setRegister_number(String register_number) {
        this.register_number = register_number;
    }

    public Element getRegister_number_element() {
        return register_number_element;
    }

    public void setRegister_number_element(Element register_number_element) {
        this.register_number_element = register_number_element;
    }

    public int getRegister_number_element_index() {
        return register_number_element_index;
    }

    public void setRegister_number_element_index(int register_number_element_index) {
        this.register_number_element_index = register_number_element_index;
    }

    public String getRegister_court() {
        return register_court;
    }

    public void setRegister_court(String register_court) {
        this.register_court = register_court;
    }

    public Element getRegister_court_element() {
        return register_court_element;
    }

    public void setRegister_court_element(Element register_court_element) {
        this.register_court_element = register_court_element;
    }

    public int getRegister_court_element_index() {
        return register_court_element_index;
    }

    public void setRegister_court_element_index(int register_court_element_index) {
        this.register_court_element_index = register_court_element_index;
    }
}
