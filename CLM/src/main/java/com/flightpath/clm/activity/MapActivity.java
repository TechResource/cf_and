package com.flightpath.clm.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.flightpath.clm.R;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.network.requests.JobsRequest;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.DrawerMenuView;
import com.flightpathcore.widgets.JobInfoWidget;
import com.flightpathcore.widgets.JobInfoWidget_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import flightpath.com.loginmodule.UpdateHelper;
import flightpath.com.mapmodule.MapCallbacks;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.activity_map)
public class MapActivity extends CLMBaseActivity implements MapCallbacks, HeaderFragment.HeaderCallback, DrawerMenuView.MenuCallbacks {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
        SynchronizationHelper.initInstance(fpModel);
    }

    @AfterViews
    protected void init(){
        headerFragment.setHeaderCallback(this);
        mapFragment.setCallbacks(this);
        menuView.setCallbacks(this);
        menuView.setupMenu(DrawerMenuView.UPDATE_APP, DrawerMenuView.STATUS, DrawerMenuView.ADD_INSPECTION, DrawerMenuView.GET_JOBS,
                DrawerMenuView.JOB_INFO, DrawerMenuView.LOGOUT, DrawerMenuView.EXIT);
        getJobs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SynchronizationHelper.getInstance().stopThread();
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

    private void onExit(){
        new android.app.AlertDialog.Builder(this).setTitle(R.string.close_app_title).setMessage(R.string.close_app_msg)
                .setPositiveButton(android.R.string.yes, (dialog, which1) -> finish())
                .setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void logout() {
        if (mapFragment.isOnTrip()) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_message)
                    .setPositiveButton(R.string.ok_label, null)
                    .show();
        } else {
            SPHelper.deleteData(this, SPHelper.USER_SESSION_KEY);
            navigator.loginActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.setStatusLabel(SPHelper.getData(this, SPHelper.CURRENT_STATUS_KEY));
    }

    private void checkUpdate() {
        fpModel.fpApi.checkUpdate(new MyCallback<UpdateAppObject>() {
            @Override
            public void onSuccess(UpdateAppObject response) {
                if (response.version != null) {
                    new UpdateHelper(MapActivity.this, response, null);
                } else {
                    new android.app.AlertDialog.Builder(MapActivity.this)
                            .setMessage("You have the newest version of app right now.")
                            .setNegativeButton("CLOSE", null)
                            .show();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void onMenuItemSelected(int id) {
        drawer.closeDrawers();
        if(id == DrawerMenuView.UPDATE_APP){
            checkUpdate();
        }else if(id == DrawerMenuView.STATUS){
            showStatusDialog();
        }else if(id == DrawerMenuView.ADD_INSPECTION){
            navigator.addInspection();
        }else if(id == DrawerMenuView.GET_JOBS){
            getJobs();
        }else if(id == DrawerMenuView.JOB_INFO) {
            showJobInfoDialog();
        }else if(id == DrawerMenuView.LOGOUT){
            logout();
        }else if(id == DrawerMenuView.EXIT){
            onExit();
        }
    }

    private void showJobInfoDialog() {
        JobInfoWidget jobInfoWidget = JobInfoWidget_.build(this);
        //TODO get proper job object
        jobInfoWidget.setJob((JobObject) DBHelper.getInstance().getLast(new JobsTable()));

        new AlertDialog.Builder(this)
                .setView(jobInfoWidget)
                .setPositiveButton(R.string.ok_label, null)
                .show();
    }

    private String[] statuses = new String[]{"On Route to destination", "Arrived at destination",
            "Awaiting client", "Vehicle handover with client", "Delivery complete", "Return journey"};

    private int getCurrentStatusIndex(){
        String curStatus = SPHelper.getData(this, SPHelper.CURRENT_STATUS_KEY);
        for (int i=0;i<statuses.length;i++){
            if(statuses[i].equalsIgnoreCase(curStatus)){
                return i;
            }
        }
        return 0;
    }

    private int tempSelectedStats = 0;

    private void showStatusDialog() {
        tempSelectedStats = getCurrentStatusIndex();
        new AlertDialog.Builder(this, R.style.BlueAlertDialog).setTitle("Status")
                .setSingleChoiceItems(statuses, tempSelectedStats, (dialog, which) -> {
                    tempSelectedStats = which;
                })
                .setPositiveButton("OK", (dialog1, which1) -> {
                    EventObject event = new EventObject();
                    event.timestamp = Utilities.getTimestamp();
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
                .show();
    }

    private void getJobs(){
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
}
