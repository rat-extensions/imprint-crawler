package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EditorInfoObject {
    private Elements elements;
    private Element element;

    private String role;
    private String name;

    public EditorInfoObject() {

    }

    public String report() {
        String report = "";
        report += "Rolle: " + role + "\n";
        report += "Name: " + name + "\n";

        return report;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
