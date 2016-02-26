package flightpath.com.inspectionmodule.widgets;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public interface InspectionWidgetInterface<T extends BaseWidgetObject> {

    Object getValue();
    void setData(T data);
    boolean isValid();
    void setValue(String value);
    String getProperty();
    T getStructure();
}
