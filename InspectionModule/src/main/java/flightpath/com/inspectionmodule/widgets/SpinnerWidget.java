package flightpath.com.inspectionmodule.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.objects.JobObject;

import org.androidannotations.annotations.EView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.flightpathcore.adapters.BaseSpinnerAdapter;

import flightpath.com.inspectionmodule.InspectionModuleInterfaces;
import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.SpinnerObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EView
public class SpinnerWidget extends Spinner implements InspectionWidgetInterface<SpinnerObject>, AdapterView.OnItemSelectedListener{

    public List<String> values = new ArrayList<>();
    private BaseSpinnerAdapter adapter;
    private SpinnerCallback callback;
    private SpinnerObject data;

    public SpinnerWidget(Context context) {
        super(context);
    }

    public SpinnerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(SpinnerCallback callback){
        this.callback = callback;
    }

    @Override
    public Object getValue() {
        if(getVisibility() == VISIBLE) {
            switch (data.spinnerType) {
                case JOBS:
                    return ((JobObject) adapter.getItem(getSelectedItemPosition())).id;
                case JOB_TYPE:
                    return values.get(getSelectedItemPosition());
            }
        }
        return null;
    }

    @Override
    public void setData(SpinnerObject data) {
        this.data = data;
        switch (data.spinnerType){
            case JOBS:
                if(getContext() instanceof InspectionModuleInterfaces.InspectionListener){
                    adapter = new BaseSpinnerAdapter(getContext(), ((InspectionModuleInterfaces.InspectionListener) getContext()).getJobs());
                }else {
                    adapter = new BaseSpinnerAdapter(getContext(), (List<JobObject>) DBHelper.getInstance().getMultiple(new JobsTable(), null));
                }
                setOnItemSelectedListener(this);
                break;
            case JOB_TYPE:
                values = Arrays.asList(getContext().getResources().getStringArray(R.array.jobs_type));
                adapter = new BaseSpinnerAdapter(getContext(), getContext().getResources().getStringArray(R.array.jobs_type));
                break;
        }
        if(adapter != null) {
            setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        if(data.value != null && !data.value.isEmpty()){
            setSelection(Integer.parseInt(data.selection));
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(String value) {
        if(data.spinnerType == SpinnerObject.SpinnerType.JOB_TYPE && value != null) {
            if (value.equalsIgnoreCase("COLLECTION")) {
                setSelection(0);
            }else if(value.equalsIgnoreCase("DELIVERY")){
                setSelection(1);
            }
        }
    }

    public void disable(){
        setEnabled(false);
        setClickable(false);
    }

    public void enable(){
        setEnabled(true);
        setClickable(true);
    }

    @Override
    public SpinnerObject getStructure() {
        data.selection = getSelectedItemPosition()+"";
        return data;
    }

    @Override
    public String getProperty() {
        if(getVisibility() == VISIBLE) {
            return data.jsonProperty;
        }else {
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(data.spinnerType == SpinnerObject.SpinnerType.JOBS && callback != null){
            callback.onJobSelected((JobObject) adapter.getItem(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface SpinnerCallback{
        void onJobSelected(JobObject job);
    }

}
