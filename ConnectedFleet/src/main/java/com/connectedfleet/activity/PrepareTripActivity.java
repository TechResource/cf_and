package com.connectedfleet.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.connectedfleet.R;
import com.flightpathcore.adapters.BaseSpinnerAdapter;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.TripTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.InputWidget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-23.
 */
@EActivity(R.layout.prepare_trip_activity)
public class PrepareTripActivity extends CFBaseActivity implements HeaderFragment.HeaderCallback {

    @FragmentByTag
    protected HeaderFragment headerFragment;
    @ViewById
    protected InputWidget startMileageIW, registrationNumber, changeMileageCode;
    @ViewById
    protected Spinner reasonSpinner;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;
    @ViewById
    protected Button changeMileageBtn;
    @ViewById
    protected LinearLayout changeMileageContainer;

    private UserObject driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(startMileageIW.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);

        driver = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID+"");
        headerFragment.setViewType(HeaderFragment.ViewType.PREPARE_TRIP);
        headerFragment.setHeaderCallback(this);
        if(driver != null)
            registrationNumber.setText(driver.vehicleRegistration);
        registrationNumber.et.addTextChangedListener(textWatcher);
        startMileageIW.et.addTextChangedListener(textWatcher);
        reasonSpinner.setAdapter(new BaseSpinnerAdapter(this, getResources().getStringArray(R.array.spinnerReasonItems))
        );
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Click
    protected void changeMileageBtn(){
        if(changeMileageCode.getValue().contentEquals(Utilities.SHA512(format.format(new Date())+driver.driverId+startMileageIW.getValue()).replaceAll("[a-zA-Z]","").substring(0, 4))){
            TripObject previousTrip = (TripObject) DBHelper.getInstance().getLast(new TripTable());
            int startMileage = -1;
            try {
                startMileage = Integer.valueOf(startMileageIW.getValue());

//                if(previousTrip.startMileage <= startMileage) {
                    EventObject changeMileageEvent = new EventObject();
                    changeMileageEvent.driverId = driver.driverId;
                    changeMileageEvent.timestamp = Utilities.getTimestamp() + "";
                    changeMileageEvent.type = EventObject.EventType.CHANGE_MILEAGE;
                    changeMileageEvent.customEventObject = startMileageIW.getValue();
                    changeMileageEvent.tripId = previousTrip.tripId;
                    changeMileageEvent.isSent = false;
                    changeMileageEvent.startDateTrip = previousTrip.startDateAsTimestamp+"";
                    TripTable tt = new TripTable();
                    previousTrip.endMileage = startMileage;
                    DBHelper.getInstance().updateOrInsert(tt, tt.getContentValues(previousTrip), previousTrip.tripId + "");
                    DBHelper.getInstance().createEvent(changeMileageEvent);
                    changeMileageContainer.setVisibility(View.GONE);
//                }else{
//                    startMileageIW.setError("Finish mileage can\'t be lower than starting mileage: " + previousTrip.startMileage + "!");
//                }
            } catch (NumberFormatException e) {
                //should never happen
            }
        }else{
            changeMileageCode.setError(getString(R.string.wrong_code_error));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPrepare();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkPrepare();
        }
    };

    private void checkPrepare() {
        boolean isPrepared = true;
        if (startMileageIW.getValue().isEmpty()) {
            isPrepared = false;
        }
        if (registrationNumber.getValue().isEmpty()) {
            isPrepared = false;
        }

        headerFragment.setRightBtnEnabled(isPrepared);
    }

    @Override
    public void onHeaderLeftBtnClick() {
        finish();
    }

    @Override
    public void onHeaderRightBtnClick() {
//        temp.setText(Utilities.SHA512(temp.getValue()).replaceAll("[a-zA-Z]","").substring(0,5));

        TripObject previousTrip = (TripObject) DBHelper.getInstance().getLast(new TripTable());

        int startMileage = -1;
        try {
            startMileage = Integer.valueOf(startMileageIW.getValue());
        } catch (NumberFormatException e) {
            //should never happen
        }
        if(previousTrip != null && previousTrip.vehicleRegistrationNumber.equals(registrationNumber.getValue().toString()) && previousTrip.endMileage > startMileage){
            askAboutMileage(previousTrip, startMileage);
        }else{
            startNewTrip(startMileage);
        }

    }
    private void startNewTrip(Integer startMileage){
        TripObject newTrip = new TripObject(reasonSpinner.getSelectedItem().toString(), startMileage, registrationNumber.getValue(), driver.driverId);
        Location l = locationHandler.getLocation();
        newTrip.startLat = l.getLatitude();
        newTrip.startLon = l.getLongitude();
        newTrip.jobId = null;
        newTrip.vehicleRegistrationNumber = registrationNumber.getValue();
        newTrip.startDateAsTimestamp = Utilities.getTimestamp();
        DBHelper.getInstance().insert(new TripTable(), new TripTable().getContentValues(newTrip));
        tripStatusHelper.startNewTrip(newTrip, l);
        if (driver.vehicleRegistration == null || !driver.vehicleRegistration.equals(registrationNumber.getValue())) {
            driver.vehicleRegistration = registrationNumber.getValue();
            DBHelper.getInstance().updateOrInsert(new DriverTable(), new DriverTable().getContentValues(driver), DriverTable.HELPER_ID+"");
        }

        finish();
    }

    private void askAboutMileage(TripObject previousTrip, Integer enterMileage) {
        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setMessage("Your last trip ended on \"" + previousTrip.endMileage + "\" for \"" + previousTrip.vehicleRegistrationNumber + "\", your starting mileage must be higher than this. Please call CLM on 01908219361 if you need to make an amendment to your last trip.")
                .setPositiveButton(R.string.ok_label, null)
                .setNeutralButton(R.string.call_label, (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:01908219361"));
                    PrepareTripActivity.this.startActivity(callIntent);
                })
                .setNegativeButton(R.string.input_code_label, (dialog1, which1) -> {
                    changeMileageContainer.setVisibility(View.VISIBLE);
                })
                .show());
    }

    @Override
    public void onMenuBtnClick() {

    }

}
