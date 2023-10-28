package impressumscrawler.datatypes;

import impressumscrawler.search_component.ImprintFinder;

public class ImprintCrawlObject {
    private ImprintObject imprintObject;
    private ImprintSearchObject imprintSearchObject;

    public ImprintCrawlObject() {

    }

    public ImprintObject getImprintObject() {
        return imprintObject;
    }

    public void setImprintObject(ImprintObject imprintObject) {
        this.imprintObject = imprintObject;
    }

    public ImprintSearchObject getImprintSearchObject() {
        return imprintSearchObject;
    }

    public void setImprintSearchObject(ImprintSearchObject imprintSearchObject) {
        this.imprintSearchObject = imprintSearchObject;
    }
}
