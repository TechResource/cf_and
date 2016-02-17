package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class DamagesObject extends BaseWidgetObject {

    public DamagesObject(String jsonProperty, Integer viewTag) {
        this.viewTag = viewTag;
        this.jsonProperty = jsonProperty;
    }

    public DamagesObject(BaseWidgetObject base) {
        super(base);
    }
}
