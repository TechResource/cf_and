package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flightpathcore.objects.BaseWidgetObject;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@EViewGroup(resName = "widget_inspection_checkbox")
public class CheckBoxWidget extends LinearLayout implements InspectionWidgetInterface<CheckBoxObject>{

    @ViewById
    protected CheckBox checkbox;
    @ViewById
    protected TextView text;
    @ViewById
    protected LinearLayout root;

    private CheckBoxObject data;

    public CheckBoxWidget(Context context) {
        super(context);
    }

    public CheckBoxWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckBoxWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnCheckedChange(CompoundButton.OnCheckedChangeListener onCheckedChange){
        checkbox.setOnCheckedChangeListener(onCheckedChange);
    }

    @Click
    protected void root(){
        checkbox.toggle();
    }

    @Override
    public Object getValue() {
        return checkbox.isChecked();
    }

    @Override
    public void setData(CheckBoxObject data) {
        this.data = data;
        checkbox.setChecked(data.isChecked);
        text.setText(data.text);
        if(!data.isPagerBlocker){
            root.setBackgroundResource(0);
            text.setBackgroundResource(0);
        }
    }

    @Override
    public boolean isValid() {
        if(data.isRequired){
            return checkbox.isChecked();
        }else {
            return true;
        }
    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public String getProperty() {
        return data.jsonProperty;
    }

    @Override
    public CheckBoxObject getStructure() {
        data.value = checkbox.isChecked()+"";
        return data;
    }

    public void setChecked(boolean checked) {
        checkbox.setChecked(checked);
    }
}
