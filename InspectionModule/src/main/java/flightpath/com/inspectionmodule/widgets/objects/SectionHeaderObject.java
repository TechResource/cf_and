package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class SectionHeaderObject extends BaseWidgetObject {
    public String title;

    public SectionHeaderObject(String title) {
        this.title = title;
    }

    public SectionHeaderObject(BaseWidgetObject base) {
        super(base);
        this.title = base.value;
    }
}
