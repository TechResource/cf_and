package flightpath.com.mapmodule;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.flightpathcore.objects.TripObject;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
@EViewGroup(resName = "widget_trip_start_stop")
public class TripStartStopWidget extends LinearLayout implements TripStatusHelper.TripStatusListener {

    @ViewById
    protected TextView tripStartLatLon, tripStartDate;
    @ViewById
    protected LinearLayout mainContainer;
    @ViewById
    protected ImageView stopBtn;
    @ViewById
    protected ToggleButton startPauseBtn;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;

    public TripStartStopWidget(Context context) {
        super(context);
        DIMapModule.diMapModule().injections().inject(this);
    }

    public TripStartStopWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        DIMapModule.diMapModule().injections().inject(this);
    }

    public TripStartStopWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DIMapModule.diMapModule().injections().inject(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TripStartStopWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isInEditMode())
            tripStatusHelper.addListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tripStatusHelper.removeListener(this);
    }

    @Click
    protected void startPauseBtn(){
        if(startPauseBtn.isChecked()){
            tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED, locationHandler.getLocation());
        }else{
            tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED, locationHandler.getLocation());
        }
    }

    @Click
    protected void stopBtn(){
        tripStatusHelper.stopTrip();
    }

    @Override
    public void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip) {
        if(trip != null) {
            SpannableStringBuilder ss = new SpannableStringBuilder("Lat: " + Utilities.formatNumber(trip.startLat, 4) + " Lng: " + Utilities.formatNumber(trip.startLon,4));
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.BOLD), (4 + Utilities.formatNumber(trip.startLat, 4).length()), (4 + Utilities.formatNumber(trip.startLat, 4).length() + 5), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tripStartLatLon.setText(ss, TextView.BufferType.SPANNABLE);
            SpannableStringBuilder ssb = new SpannableStringBuilder("Started at: "+Utilities.getDateFromTimestamp(trip.startDateAsTimestamp));
            ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tripStartDate.setText(ssb, TextView.BufferType.SPANNABLE);
        }
        switch (tripStatus){
            case TRIP_STARTED:
                startPauseBtn.setChecked(true);
                break;
            case TRIP_PAUSED:
                startPauseBtn.setChecked(false);
                break;
        }
    }
}
