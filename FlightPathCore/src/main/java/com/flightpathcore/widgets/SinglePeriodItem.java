package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flightpathcore.objects.PeriodResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-14.
 */
@EViewGroup(resName = "single_period_item")
public class SinglePeriodItem extends LinearLayout {

    @ViewById
    protected TextView startMileage, endMileage, startDate, endDate, businessMileage, privateMileage, vehicleReg;
    private PeriodResponse.Trip trip;


    public SinglePeriodItem(Context context) {
        super(context);
    }

    public SinglePeriodItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SinglePeriodItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SinglePeriodItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init() {
        if (trip != null) {
            fillView();
        }
    }

    private void fillView() {
        startMileage.setText(trip.startMileage != null ? trip.startMileage + "" : "");
        endMileage.setText(trip.endMileage != null ? trip.endMileage + "" : "");
        startDate.setText(trip.startDate != null ? trip.startDate+"" : "");
        endDate.setText(trip.endDate != null ? trip.endDate+"" : "");
        vehicleReg.setText(trip.vehicleReg != null ? trip.vehicleReg+"" : "");
        businessMileage.setText(trip.businessMileage != null ? trip.businessMileage+"" : "");
        privateMileage.setText(trip.privateMileage != null ? trip.privateMileage+"" : "");
    }

    public void setData(PeriodResponse.Trip trip) {
        this.trip = trip;
        if (startMileage != null) {
            fillView();
        }
    }

}
