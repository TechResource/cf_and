package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-07.
 */
public class DamagesWithSquashedFrogObject extends BaseWidgetObject {
    public long currentEventId;

    public DamagesWithSquashedFrogObject(String jsonProperty, Integer viewTag, long currentEventId) {
        this.viewTag = viewTag;
        this.jsonProperty = jsonProperty;
        this.currentEventId = currentEventId;
    }

    public DamagesWithSquashedFrogObject(BaseWidgetObject base,  long currentEventId) {
        super(base);
        this.currentEventId = currentEventId;
    }
}
