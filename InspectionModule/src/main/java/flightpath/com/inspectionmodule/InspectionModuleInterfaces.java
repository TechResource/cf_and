package flightpath.com.inspectionmodule;

import java.util.List;

import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.objects.JobObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class InspectionModuleInterfaces {

    public interface InspectionContainerCallback{
        List<BaseWidgetObject> getWidgetsStep1();
        List<BaseWidgetObject> getWidgetStep2();
    }

    public interface InspectionListener {
        void onCompleteListener();

        /**
         * @return in most cases should be (List<JobObject>) DBHelper.getInstance().getMultiple(new JobsTable(), null))
         */
        List<JobObject> getJobs();
    }
}
