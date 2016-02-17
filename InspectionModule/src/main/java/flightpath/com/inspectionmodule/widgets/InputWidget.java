package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-12.
 */
@EViewGroup(resName = "widget_inspection_input")
public class InputWidget extends FrameLayout implements InspectionWidgetInterface<InputObject> {

    @ViewById
    protected EditText et;
    @ViewById
    protected TextInputLayout input;
    private InputObject data;

    public InputWidget(Context context) {
        super(context);
    }

    public InputWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setData(InputObject data) {
        this.data = data;
        input.setHint(data.hint + (data.isRequired ? "*" : ""));
        if (data.value != null && !data.value.isEmpty()) {
            et.setText(data.value);
        }
//        et.setFocusable(data.isEditable());
//        et.setClickable(data.isEditable());
    }

    @Override
    public void setValue(String value) {
        et.setText(value == null ? "" : value);
    }

    @Override
    public String getProperty() {
        return data.jsonProperty;
    }

    @Override
    public boolean isValid() {
        if (data.isRequired) {
            boolean isValid = !et.getText().toString().trim().isEmpty();
            if(!isValid){
                et.setError(getResources().getString(R.string.field_required_error));
            }
            return isValid;
        } else {
            return true;
        }
    }

    @Override
    public Object getValue() {
        return et.getText().toString().trim();
    }

    public String getContentValue() {
        return et.getText().toString().trim();
    }
}
