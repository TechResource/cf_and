package com.connectedfleet.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.connectedfleet.R;
import com.flightpathcore.acceleration.AccelerationService;
import com.flightpathcore.adapters.PagerAdapter;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.SwipeableViewPager;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.DrawerMenuView;
import com.flightpathcore.widgets.InputWidget;
import com.flightpathcore.widgets.InputWidget_;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WakeLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import flightpath.com.donglemodule.DongleContainerFragment;
import flightpath.com.donglemodule.DongleContainerFragment_;
import flightpath.com.donglemodule.DongleDataHelper;
import flightpath.com.loginmodule.UpdateHelper;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.MapCallbacks;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.MapFragment_;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
@EActivity(R.layout.main_activity)
public class MainActivity extends CFBaseActivity implements HeaderFragment.HeaderCallback, MapCallbacks, DrawerMenuView.MenuCallbacks,
        TripStatusHelper.TripStatusListener, TripStatusHelper.StopTripListener, DrawerLayout.DrawerListener {

    @FragmentByTag
    protected HeaderFragment headerFragment;
    @ViewById
    protected SwipeableViewPager pager;
    @ViewById
    protected PagerTabStrip pagerTabStrip;
    @ViewById
    protected DrawerMenuView menuView;
    @ViewById
    protected DrawerLayout drawer;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;
    @Inject
    protected FPModel fpModel;

    protected DongleDataHelper dongleDataHelper;

    private PagerAdapter adapter;
    private MapFragment mapFragment;
    private DongleContainerFragment dongleFragment;
    private List<String> fragments;
    private Handler handler = new Handler();
    private UserObject currentUser = null;
    private AccelerationService accelerationService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
        SynchronizationHelper.initInstance(fpModel);
        currentUser = ((UserObject) DBHelper.getInstance().getLast(new DriverTable()));
        if(currentUser != null) {
            pointCollectInterval = 1000 * currentUser.gpsPointPer;
        }
        tripStatusHelper.addListener(this);
        tripStatusHelper.setStopTripListener(this);
        handler.post(dataCollector);


        Intent i = new Intent(this, AccelerationService.class);
        i.addCategory(AccelerationService.tag);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @AfterViews
    protected void init() {
        menuView.setCallbacks(this);
        menuView.setupMenu(DrawerMenuView.UPDATE_APP, DrawerMenuView.CLOSE_PERIOD, DrawerMenuView.LOGOUT, DrawerMenuView.EXIT);

        drawer.setDrawerListener(this);

        headerFragment.setHeaderCallback(this);
        headerFragment.setViewType(HeaderFragment.ViewType.MAIN_ACTIVITY);
        pager.setOffscreenPageLimit(2);
        fragments = new ArrayList<>();
        fragments.add("android:switcher:" + pager.getId() + ":" + 0);
        fragments.add("android:switcher:" + pager.getId() + ":" + 1);
        if (mapFragment == null) {
            mapFragment = MapFragment_.builder().build();
            mapFragment.setCallbacks(this);
            getSupportFragmentManager().beginTransaction().add(pager.getId(), mapFragment, fragments.get(0)).commit();
        }

        if (dongleFragment == null) {
            dongleFragment = DongleContainerFragment_.builder().build();
            getSupportFragmentManager().beginTransaction().add(pager.getId(), dongleFragment, fragments.get(1)).commit();
        }
        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            AccelerationService.MyBinder b = (AccelerationService.MyBinder) binder;
            accelerationService = b.getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            accelerationService = null;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onHeaderLeftBtnClick() {

    }

    @Override
    public void onHeaderRightBtnClick() {

    }

    @Override
    public void onMenuBtnClick() {
        drawer.openDrawer(menuView);
    }

    private void logout() {
        if (mapFragment.isOnTrip()) {
            Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_ont_trip_message)
                    .setPositiveButton(R.string.ok_label, null)
                    .show());
        } else {
            Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_message)
                    .setPositiveButton(R.string.ok_label, (dialog, which) -> {
                        SPHelper.deleteData(this, SPHelper.USER_SESSION_KEY);
                        navigator.splashScreen();
                    })
                    .setNegativeButton(R.string.cancel_text, null)
                    .show());
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
        } else {
            onExit();
        }
    }

    private void onExit(){
        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setTitle(R.string.close_app_title)
                .setMessage(R.string.close_app_msg)
                .setPositiveButton(R.string.yes_label, (dialog, which1) -> MainActivity.this.finish())
                .setNegativeButton(R.string.no_label, null)
                .show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SynchronizationHelper.getInstance().stopThread();
        headerFragment = null;
        handler.removeCallbacks(dataCollector);
        unbindService(mConnection);
    }

    @Override
    public void onPrepareTrip() {
        navigator.prepareTrip();
    }

    private Integer onMenuCloseOpenViewWithId = null;

    @Override
    public void onMenuItemSelected(int id) {
        onMenuCloseOpenViewWithId = id;
        drawer.closeDrawers();
    }

    private void checkUpdate() {
        fpModel.fpApi.checkUpdate(new MyCallback<UpdateAppObject>() {
            @Override
            public void onSuccess(UpdateAppObject response) {
                if(response.version != null){
                    new UpdateHelper(MainActivity.this, response, null).askUpdateApp();
                }else{
                    Utilities.styleAlertDialog(new AlertDialog.Builder(MainActivity.this, R.style.BlueAlertDialog)
                            .setMessage("You have the newest version of app right now.")
                            .setNegativeButton("CLOSE", null)
                            .show());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Location currentLocation = null;
    private TripObject.TripStatus currentTripStatus = null;
    private Map<String, String> currentDongleData = null;
    private Gson gson = new Gson();
    private int pointCollectInterval = 1000;
    private boolean shouldCollectData = false;

    private Runnable dataCollector = new Runnable() {
        @Override
        public void run() {
            if(shouldCollectData) {
                EventObject eventObject = collectPoint();
                if (eventObject != null) {
                    DBHelper.getInstance().createEvent(eventObject);
                }
            }
            handler.postDelayed(this, pointCollectInterval);
        }
    };

    @WakeLock(tag = "point_collector", level = WakeLock.Level.PARTIAL_WAKE_LOCK)
    protected EventObject collectPoint() {
        EventObject eventObject = null;
        currentLocation = locationHandler.getLocation();
        currentTripStatus = tripStatusHelper.getTripStatus();

        if(dongleDataHelper == null)
            dongleDataHelper = ((DongleContainerFragment)adapter.getItem(1)).getDongleDataHelper();

        if(dongleDataHelper != null)
            currentDongleData = dongleDataHelper.getCurrentData();

        if(mConnection != null && accelerationService != null && currentDongleData != null){
            currentDongleData.put("acceleration", accelerationService.getCurrentAcceleration()+"");
        }

        if(currentLocation != null && tripStatusHelper.getTripStatus() != TripObject.TripStatus.TRIP_STOPPED) {
            eventObject = new EventObject();

            eventObject.type = EventObject.EventType.POINT;
            if (dongleDataHelper.isDongleConnected()) {
                eventObject.dongleEnabled = 1;
                eventObject.btEnabled = 1;
            } else {
                eventObject.dongleEnabled = 0;
                eventObject.btEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled() ? 1 : 0;
            }

            if (currentDongleData != null) {
                eventObject.customEventObject = gson.toJson(currentDongleData);
            }
            eventObject.driverId = currentUser.driverId;
            eventObject.isSent = false;
            eventObject.latitude = currentLocation.getLatitude();
            eventObject.longitude = currentLocation.getLongitude();
            if (currentTripStatus == TripObject.TripStatus.TRIP_PAUSED) {
                eventObject.onPause = true;
            } else {
                eventObject.onPause = false;
            }
            eventObject.timestamp = Utilities.getTimestamp();
            if(tripStatusHelper.getCurrentTrip() != null) {
                eventObject.tripId = tripStatusHelper.getCurrentTrip().tripId;
                eventObject.startDateTrip = tripStatusHelper.getCurrentTrip().startDateAsTimestamp+"";
            }

            Log.d("POINT COLLECTOR", gson.toJson(eventObject));
        }
        return eventObject;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mapFragment != null){
            mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip) {
        switch (tripStatus){
            case TRIP_STARTED:
            case TRIP_PAUSED:
                shouldCollectData = true;
                break;
            case TRIP_STOPPED:
                shouldCollectData = false;
                break;
        }
    }

    private boolean canFinish = false;

    @Override
    public void onStopTrip() {
        InputWidget inputWidget = InputWidget_.build(this);
        inputWidget.setOnlyNumbersInput();
        inputWidget.setText(null, getString(R.string.end_mileage_label));
        AlertDialog d = new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setView(inputWidget)
                .setPositiveButton(R.string.ok_label, (dialog1, which) -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(inputWidget.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                })
                .setNegativeButton(R.string.cancel_text, (dialog1, which) -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(inputWidget.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                })
                .setCancelable(false)
                .create();
        d.setOnShowListener(dialog -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(inputWidget.et, InputMethodManager.SHOW_IMPLICIT);
        });
        d.show();
        Utilities.styleAlertDialog(d);
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Integer endMileage = null;
            try {
                endMileage = Integer.parseInt(inputWidget.getValue());
            } catch (Exception e) {
                endMileage = null;
            }
            if (endMileage != null && endMileage >= tripStatusHelper.getCurrentTrip().startMileage) {
                tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED, locationHandler.getLocation(), endMileage);
                canFinish = true;
            } else {
                inputWidget.setError("Finish mileage can\'t be lower than starting mileage: " + tripStatusHelper.getCurrentTrip().startMileage + "!");
                canFinish = false;
            }
            if (canFinish) {
                d.dismiss();
            }
        });

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if(onMenuCloseOpenViewWithId != null){
            if (onMenuCloseOpenViewWithId == DrawerMenuView.UPDATE_APP) {
                checkUpdate();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.LOGOUT) {
                logout();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.EXIT) {
                onExit();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.CLEAR_DB) {
                logout();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.CLOSE_PERIOD) {
                navigator.closePeriod();
            }
        }
        onMenuCloseOpenViewWithId = null;
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
