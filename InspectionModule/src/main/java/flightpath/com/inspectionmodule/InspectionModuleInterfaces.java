package flightpath.com.inspectionmodule;

import java.util.List;

import com.flightpathcore.objects.BaseWidgetObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class InspectionModuleInterfaces {

    public interface InspectionContainerCallback{
        List<BaseWidgetObject> getWidgetsStep1();
        List<BaseWidgetObject> getWidgetStep2();
    }
}
