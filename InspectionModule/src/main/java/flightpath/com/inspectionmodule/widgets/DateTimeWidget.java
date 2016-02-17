package flightpath.com.inspectionmodule.widgets;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import flightpath.com.inspectionmodule.R;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-18.
 */
@EViewGroup(resName = "widget_inspection_date")
public class DateTimeWidget extends FrameLayout implements InspectionWidgetInterface<InputObject> {

    @ViewById
    protected EditText et;
    @ViewById
    protected TextInputLayout input;
    @ViewById
    protected FrameLayout root;
    private InputObject data;
    private int year, month, dayOfMonth;

    public DateTimeWidget(Context context) {
        super(context);
    }

    public DateTimeWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateTimeWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setData(InputObject data) {
        this.data = data;
        input.setHint(data.hint);
        if (data.value != null && !data.value.isEmpty()) {
            et.setText(data.value);
        }
    }

    @Click
    protected void et(){
        createDialogWithDateAndTimePicker();
    }

    @Click
    protected void input(){
        createDialogWithDateAndTimePicker();
    }

    @Click
    protected void root(){
        createDialogWithDateAndTimePicker();
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
        return data.jsonProperty;
    }

    private void createDialogWithDateAndTimePicker() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Date");
        dialog.setContentView(R.layout.date_and_time_picker_layout);
        dialog.setCancelable(true);
        dialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DateTimeWidget.this.year = year;
                DateTimeWidget.this.month = monthOfYear;
                DateTimeWidget.this.dayOfMonth = dayOfMonth;
            }
        });

        dialog.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date(year - 1900, month, dayOfMonth));
                et.setText(date);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
