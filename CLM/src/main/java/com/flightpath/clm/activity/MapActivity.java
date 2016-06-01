package com.flightpath.clm.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flightpath.clm.R;
import com.flightpathcore.acceleration.AccelerationService;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.network.requests.JobsRequest;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.DrawerMenuView;
import com.flightpathcore.widgets.InputWidget;
import com.flightpathcore.widgets.InputWidget_;
import com.flightpathcore.widgets.JobInfoWidget;
import com.flightpathcore.widgets.JobInfoWidget_;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WakeLock;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import flightpath.com.loginmodule.UpdateHelper;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.MapCallbacks;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.activity_map)
public class MapActivity extends CLMBaseActivity implements MapCallbacks, HeaderFragment.HeaderCallback, DrawerMenuView.MenuCallbacks,
        TripStatusHelper.StopTripListener, TripStatusHelper.TripStatusListener, DrawerLayout.DrawerListener {

    @FragmentByTag
    protected MapFragment mapFragment;
    @FragmentByTag
    protected HeaderFragment headerFragment;
    @ViewById
    protected DrawerMenuView menuView;
    @ViewById
    protected DrawerLayout drawer;
    @Inject
    protected FPModel fpModel;
    @Inject
    protected TripStatusHelper tripStatusHelper;
    @Inject
    protected LocationHandler locationHandler;

    private Handler handler = new Handler();
    private UserObject currentUser = null;
    private AccelerationService accelerationService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
        currentUser = ((UserObject) DBHelper.getInstance().getLast(new DriverTable()));
        if (currentUser != null) {
            if(!BaseApplication.isDebug(this))
                Crashlytics.setUserIdentifier(currentUser.driverId + "");
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
        headerFragment.setHeaderCallback(this);
        mapFragment.setCallbacks(this);

        menuView.setCallbacks(this);
        menuView.setupMenu(DrawerMenuView.UPDATE_APP, DrawerMenuView.STATUS, DrawerMenuView.ADD_INSPECTION, DrawerMenuView.DISPOSAL_INSPECTION, DrawerMenuView.GET_JOBS,
                /*DrawerMenuView.JOB_INFO, */DrawerMenuView.LOGOUT, DrawerMenuView.EXIT);

        drawer.setDrawerListener(this);

        getJobs();
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

    @Override
    public void onBackPressed() {
        onExit();
    }

    private void onExit() {
        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setTitle(R.string.close_app_title)
                .setMessage(R.string.close_app_msg)
                .setPositiveButton(R.string.yes_label, (dialog, which1) -> MapActivity.this.finish())
                .setNegativeButton(R.string.no_label, null)
                .show());
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
    protected void onResume() {
        super.onResume();
        SynchronizationHelper.initInstance(fpModel);
        DBHelper.getInstance().setLocationHandler(locationHandler);

        mapFragment.setStatusLabel(SPHelper.getData(this, SPHelper.CURRENT_STATUS_KEY));
    }

    private void checkUpdate() {
        fpModel.fpApi.checkUpdate(new MyCallback<UpdateAppObject>() {
            @Override
            public void onSuccess(UpdateAppObject response) {
                if (response.version != null) {
                    new UpdateHelper(MapActivity.this, response, null).askUpdateApp();
                } else {
                    Utilities.styleAlertDialog(new AlertDialog.Builder(MapActivity.this, R.style.BlueAlertDialog)
                            .setMessage("You have the newest version of app right now.")
                            .setNegativeButton("CLOSE", null)
                            .show());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Integer onMenuCloseOpenViewWithId = null;

    @Override
    public void onMenuItemSelected(int id) {
        onMenuCloseOpenViewWithId = id;
        drawer.closeDrawers();
    }

    private void showJobInfoDialog() {
        JobInfoWidget jobInfoWidget = JobInfoWidget_.build(this);
        //TODO get proper job object
        jobInfoWidget.setJob((JobObject) DBHelper.getInstance().getLast(new JobsTable()));

        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setView(jobInfoWidget)
                .setPositiveButton(R.string.ok_label, null)
                .show());
    }

    private String[] statuses = new String[]{"On Route to destination", "Arrived at destination",
            "Awaiting client", "Vehicle handover with client", "Delivery complete", "Return journey"};

    private int getCurrentStatusIndex() {
        String curStatus = SPHelper.getData(this, SPHelper.CURRENT_STATUS_KEY);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(curStatus)) {
                return i;
            }
        }
        return 0;
    }

    private int tempSelectedStats = 0;

    private void showStatusDialog() {
        tempSelectedStats = getCurrentStatusIndex();
        Utilities.styleAlertDialog(new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setTitle("Status")
                .setSingleChoiceItems(statuses, tempSelectedStats, (dialog, which) -> {
                    tempSelectedStats = which;
                })
                .setPositiveButton("OK", (dialog1, which1) -> {
                    EventObject event = new EventObject();
                    event.timestamp = Utilities.getTimestamp() + "";
                    event.type = EventObject.EventType.STATUS;
                    event.customEventObject = statuses[tempSelectedStats];
                    if (tripStatusHelper.getCurrentTrip() != null) {
                        event.startDateTrip = tripStatusHelper.getCurrentTrip().startDateAsTimestamp + "";
                    } else {
                        event.startDateTrip = null;
                    }
                    DBHelper.getInstance().createEvent(event);
                    SPHelper.saveData(MapActivity.this, SPHelper.CURRENT_STATUS_KEY, statuses[tempSelectedStats]);
                    mapFragment.setStatusLabel(statuses[tempSelectedStats]);
                })
                .setNegativeButton("CANCEL", null)
                .show());
    }

    private void getJobs() {
        fpModel.fpApi.getJobs(new JobsRequest(SPHelper.getUserSession(this)), new MyCallback<List<JobObject>>() {
            @Override
            public void onSuccess(List<JobObject> response) {
                DBHelper.getInstance().insertMultiple(new JobsTable(), new JobsTable().getMultipleValues(response));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MapActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean canFinish = false;
    private AlertDialog stopTripDialog = null;

    @Override
    public void onStopTrip() {
        InputWidget inputWidget = InputWidget_.build(this);
        inputWidget.setOnlyNumbersInput();
        inputWidget.setText(null, getString(R.string.end_mileage_label));
        inputWidget.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (stopTripDialog != null)
                    stopTripDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            } else {
                return false;
            }
        });
        stopTripDialog = new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                .setView(inputWidget)
                .setPositiveButton(R.string.ok_label, (dialog1, which) -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputWidget.et, InputMethodManager.SHOW_IMPLICIT);
                })
                .setNegativeButton(R.string.cancel_text, (dialog1, which) -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(inputWidget.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                })
                .setCancelable(false)
                .create();
        stopTripDialog.setOnShowListener(dialog -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(inputWidget.et, InputMethodManager.SHOW_IMPLICIT);
        });
        stopTripDialog.show();
        Utilities.styleAlertDialog(stopTripDialog);
        stopTripDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Integer endMileage = null;
            try {
                endMileage = Integer.parseInt(inputWidget.getValue());
            } catch (Exception e) {
                endMileage = null;
            }
//            if (endMileage != null && endMileage >= tripStatusHelper.getCurrentTrip().startMileage) {
            tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED, locationHandler.getLocation(), endMileage);
            canFinish = true;
//            } else {
//                inputWidget.setError("Finish mileage can\'t be lower than starting mileage: " + tripStatusHelper.getCurrentTrip().startMileage + "!");
//                canFinish = false;
//            }
            if (canFinish) {
                if (stopTripDialog != null)
                    stopTripDialog.dismiss();
            }
        });
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

    private Location currentLocation = null;
    private TripObject.TripStatus currentTripStatus = null;
    private Map<String, String> currentDongleData = null;
    private Gson gson = new Gson();
    private int pointCollectInterval = 1000;
    private boolean shouldCollectData = false;

    private Runnable dataCollector = new Runnable() {
        @Override
        public void run() {
            if (shouldCollectData) {
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

        if (mConnection != null && accelerationService != null && currentDongleData != null) {
            currentDongleData.put("acceleration", accelerationService.getCurrentAcceleration() + "");
        }

        if (currentLocation != null && tripStatusHelper.getTripStatus() != TripObject.TripStatus.TRIP_STOPPED) {
            eventObject = new EventObject();

            eventObject.type = EventObject.EventType.POINT;

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
            eventObject.timestamp = Utilities.getTimestamp() + "";
            if (tripStatusHelper.getCurrentTrip() != null) {
                eventObject.tripId = tripStatusHelper.getCurrentTrip().tripId;
                eventObject.startDateTrip = tripStatusHelper.getCurrentTrip().startDateAsTimestamp + "";
            }

            Log.d("POINT COLLECTOR", gson.toJson(eventObject));
        }
        return eventObject;
    }

    @Override
    public void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip) {
        switch (tripStatus) {
            case TRIP_STARTED:
            case TRIP_PAUSED:
                shouldCollectData = true;
                break;
            case TRIP_STOPPED:
                shouldCollectData = false;
                break;
        }
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (onMenuCloseOpenViewWithId != null) {
            if (onMenuCloseOpenViewWithId == DrawerMenuView.UPDATE_APP) {
                checkUpdate();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.STATUS) {
                showStatusDialog();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.ADD_INSPECTION) {
                navigator.addInspection();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.GET_JOBS) {
                getJobs();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.JOB_INFO) {
                showJobInfoDialog();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.LOGOUT) {
                logout();
            } else if (onMenuCloseOpenViewWithId == DrawerMenuView.EXIT) {
                onExit();
            } else if( onMenuCloseOpenViewWithId == DrawerMenuView.DISPOSAL_INSPECTION){
                navigator.addDisposalInspection();
            }
        }

        onMenuCloseOpenViewWithId = null;
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
