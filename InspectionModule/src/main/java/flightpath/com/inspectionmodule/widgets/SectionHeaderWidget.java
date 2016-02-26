package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.androidannotations.annotations.EView;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.SectionHeaderObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EView
public class SectionHeaderWidget extends TextView implements InspectionWidgetInterface<SectionHeaderObject>{

    private SectionHeaderObject data;

    public SectionHeaderWidget(Context context) {
        super(context);
    }

    public SectionHeaderWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionHeaderWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SectionHeaderWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setData(SectionHeaderObject data) {
        this.data = data;
        setText(data.title);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public SectionHeaderObject getStructure() {
        return data;
    }

    @Override
    public String getProperty() {
        return null;
    }
}
