package flightpath.com.inspectionmodule.widgets.objects;

import com.flightpathcore.objects.BaseWidgetObject;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
public class SpinnerObject extends BaseWidgetObject {

    public SpinnerType spinnerType;
    public List<Object> items = null;

    public SpinnerObject(BaseWidgetObject base){
        super(base);
        if(base.value.equalsIgnoreCase("jobs")){
            spinnerType = SpinnerType.JOBS;
        }else if(base.value.equalsIgnoreCase("job_type")){
            spinnerType = SpinnerType.JOB_TYPE;
        }
    }

    public SpinnerObject(String jsonProperty, SpinnerType spinnerType, Integer viewTag) {
        this.jsonProperty = jsonProperty;
        this.spinnerType = spinnerType;
        this.viewTag = viewTag;
    }

    public enum SpinnerType{
        JOBS,
        JOB_TYPE
    }
}
