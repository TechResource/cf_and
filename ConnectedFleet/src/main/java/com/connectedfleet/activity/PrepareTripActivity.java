package com.connectedfleet.activity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import com.connectedfleet.R;
import com.flightpathcore.adapters.BaseSpinnerAdapter;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.TripTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.InputWidget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

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
    protected InputWidget startMileageIW, registrationNumber;
    @ViewById
    protected Spinner reasonSpinner;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;

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
        reasonSpinner.setAdapter(new BaseSpinnerAdapter(this, getResources().getStringArray(R.array.spinnerReasonItems)));
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
            DBHelper.getInstance().updateOrInsert(new DriverTable(), new DriverTable().getContentValues(driver), driver.driverId + "");
        }

        finish();
    }

    private void askAboutMileage(TripObject previousTrip, Integer enterMileage) {
        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setMessage("Last trip ended with " + previousTrip.endMileage + " mileage for " + previousTrip.vehicleRegistrationNumber + " vehicle.")
                .setPositiveButton(R.string.ok_label, null)
                .show());
    }

    @Override
    public void onMenuBtnClick() {

    }
}
