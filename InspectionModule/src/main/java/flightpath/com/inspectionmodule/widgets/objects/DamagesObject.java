package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class DamagesObject extends BaseWidgetObject {

    public long currentEventId;

    public DamagesObject(String jsonProperty, Integer viewTag, long currentEventId) {
        this.viewTag = viewTag;
        this.jsonProperty = jsonProperty;
        this.currentEventId = currentEventId;
    }

    public DamagesObject(BaseWidgetObject base,  long currentEventId) {
        super(base);
        this.currentEventId = currentEventId;
    }
}
