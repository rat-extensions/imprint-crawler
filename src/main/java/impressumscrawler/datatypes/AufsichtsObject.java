package impressumscrawler.datatypes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AufsichtsObject {

    //private Element element;
    private Elements elements;

    private Elements elements_with_structural_indicator;

    private boolean has_aufsichts_indicator;
    private String Aufsichtebehoerde;

    public AufsichtsObject() {

    }

    public String report() {
        return getAufsichtebehoerde();
    }

/*
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
*/

    public boolean isHas_indicator() {
        return has_aufsichts_indicator;
    }

    public void setHas_indicator(boolean has_indicator) {
        this.has_aufsichts_indicator = has_indicator;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public Elements getElements_with_structural_indicator() {
        return elements_with_structural_indicator;
    }

    public void setElements_with_structural_indicator(Elements elements_with_structural_indicator) {
        this.elements_with_structural_indicator = elements_with_structural_indicator;
    }

    public String getAufsichtebehoerde() {
        return Aufsichtebehoerde;
    }

    public void setAufsichtebehoerde(String aufsichtebehoerde) {
        Aufsichtebehoerde = aufsichtebehoerde;
    }
}
