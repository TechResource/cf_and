package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class CheckBoxObject extends BaseWidgetObject {
    public String text;
    public boolean isChecked;
    public boolean isPagerBlocker = false;

    public CheckBoxObject(String jsonProperty, String text, boolean isChecked, boolean isRequired, boolean isPagerBlocker) {
        this.jsonProperty = jsonProperty;
        this.text = text;
        this.isChecked = isChecked;
        this.isRequired = isRequired;
        this.isPagerBlocker = isPagerBlocker;
    }

    public CheckBoxObject(BaseWidgetObject base) {
        super(base);
        if(base.value != null){
            this.isChecked = Boolean.parseBoolean(base.value);
        }
        this.text = base.hint;
        this.isPagerBlocker = true;
    }
}
