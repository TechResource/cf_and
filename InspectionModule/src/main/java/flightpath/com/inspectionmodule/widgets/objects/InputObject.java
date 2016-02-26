package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class InputObject extends BaseWidgetObject {

    public InputObject(String jsonProperty, String hint, String value, Integer viewTag, boolean isRequired, boolean isEditable) {
        this.jsonProperty = jsonProperty;
        this.hint = hint;
        this.value = value;
        this.viewTag = viewTag;
        this.isRequired = isRequired;
        this.isEditable = isEditable;
    }

    public InputObject(BaseWidgetObject base) {
        super(base);
        this.hint = base.hint;
        this.value = base.value;
    }
}
