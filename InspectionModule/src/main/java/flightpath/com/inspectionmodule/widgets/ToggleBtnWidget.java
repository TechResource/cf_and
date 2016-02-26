package flightpath.com.inspectionmodule.widgets;

import com.google.gson.JsonObject;

import flightpath.com.inspectionmodule.widgets.objects.ToggleBtnObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-15.
 */
public class ToggleBtnWidget implements InspectionWidgetInterface<ToggleBtnObject> {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setData(ToggleBtnObject data) {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getProperty() {
        return null;
    }

    @Override
    public ToggleBtnObject getStructure() {
        return null;
    }
}
