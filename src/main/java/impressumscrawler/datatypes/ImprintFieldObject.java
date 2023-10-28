package impressumscrawler.datatypes;


import impressumscrawler.extraction_component.ImprintExtraction.ImprintExtractor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Comparator;

public class ImprintFieldObject implements Comparator {
    private Elements elements;
    private Element element;

    @Override
    public int compare(Object o1, Object o2) {
        ImprintFieldObject ifo1 = (ImprintFieldObject) o1;
        ImprintFieldObject ifo2 = (ImprintFieldObject) o2;
        return ifo1.elements.indexOf(element) - ifo2.elements.indexOf(element);
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
}
