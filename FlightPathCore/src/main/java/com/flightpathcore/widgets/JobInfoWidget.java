package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.objects.JobObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EViewGroup(resName = "widget_job_info")
public class JobInfoWidget extends ScrollView {

    @ViewById
    protected LinearLayout jobInfoContainer;

    private JobObject job;

    public JobInfoWidget(Context context) {
        super(context);
    }

    public JobInfoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JobInfoWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JobInfoWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init(){
        if(job != null){
            fillView();
        }
    }

    public void setJob(JobObject job){
        this.job = job;
        if(jobInfoContainer != null){
            fillView();
        }
    }

    private void fillView(){
        jobInfoContainer.addView(getSimpleSectionHeader(R.string.job_description_label));
        if(job.booking != null && job.booking.customer != null && !job.booking.customer.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.booking.customer, R.string.customer_hint));
        if(job.number != null && !job.number.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.number, R.string.number_hint));
        if(job.address != null && !job.address.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.address, R.string.address_hint));
        if(job.description != null && !job.description.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.description, R.string.job_description_label));
        if(job.booking != null && job.booking.col_mobile != null && !job.booking.col_mobile.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.booking.col_mobile, R.string.job_phone_hint));
        if(job.booking != null && job.booking.col_contact_name != null && !job.booking.col_contact_name.isEmpty() )
            jobInfoContainer.addView(getSimpleInputView(job.booking.col_contact_name, R.string.job_contact_hint));
        if(job.dateFrom != null && job.dateTo != null && !job.dateFrom.isEmpty() && !job.dateTo.isEmpty())
            jobInfoContainer.addView(getSimpleInputView(job.dateFrom+" to: "+job.dateTo, R.string.job_time_hint));
        if(job.vehicle != null) {
            jobInfoContainer.addView(getSimpleSectionHeader(R.string.car_description_label));
            if(job.vehicle.model != null && job.vehicle.model.name != null && !job.vehicle.model.name.isEmpty())
                jobInfoContainer.addView(getSimpleInputView(job.vehicle.model.name, R.string.model_name_hint));
            if(job.vehicle.registration != null && !job.vehicle.registration.isEmpty())
                jobInfoContainer.addView(getSimpleInputView(job.vehicle.registration, R.string.registration_hint));
            if(job.vehicle.manufacturer != null && job.vehicle.manufacturer.name != null && !job.vehicle.manufacturer.name.isEmpty())
                jobInfoContainer.addView(getSimpleInputView(job.vehicle.manufacturer.name, R.string.manufacturer_hint));
        }
        if(job.notes != null && !job.notes.isEmpty()){
            jobInfoContainer.addView(getSimpleSectionHeader(R.string.notes_label));
            jobInfoContainer.addView(getSimpleInputView(job.notes, null));
            if(job.extraNotes != null && !job.extraNotes.isEmpty()){
                jobInfoContainer.addView(getSimpleInputView(job.extraNotes, null));
            }
        }

    }

    private View getSimpleSectionHeader(int textRes){
        return getSimpleSectionHeader(getResources().getString(textRes));
    }

    private View getSimpleSectionHeader(String text){
        TextView view = new TextView(new ContextThemeWrapper(getContext(), R.style.Base_SectionHeader));
        view.setText(text);
        return view;
    }

    private View getSimpleInputView(String text, int hintRes){
        return getSimpleInputView(text, getResources().getString(hintRes));
    }

    private View getSimpleInputView(String text, String hint){
        InputWidget inputWidget = InputWidget_.build(getContext());
        inputWidget.setText(text, hint);
        return inputWidget;
    }
}
