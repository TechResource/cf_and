package com.runway.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.network.requests.JobsRequest;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.widgets.DrawerMenuView;
import com.runway.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import flightpath.com.loginmodule.UpdateHelper;
import flightpath.com.mapmodule.MapCallbacks;
import flightpath.com.mapmodule.MapFragment;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-14.
 */
@EActivity(R.layout.activity_runway_map)
public class RunwayMapActivity extends RunwayBaseActivity implements MapCallbacks, HeaderFragment.HeaderCallback, DrawerMenuView.MenuCallbacks {

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
        menuView.setupMenu(DrawerMenuView.UPDATE_APP, DrawerMenuView.ADD_INSPECTION, DrawerMenuView.GET_JOBS,
                DrawerMenuView.JOB_INFO, DrawerMenuView.LOGOUT, DrawerMenuView.EXIT);
        getJobs();
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
        new AlertDialog.Builder(this, R.style.BlueAlertDialog).setTitle(R.string.close_app_title).setMessage(R.string.close_app_msg)
                .setPositiveButton(android.R.string.yes, (dialog, which1) -> finish())
                .setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void logout() {
        if (mapFragment.isOnTrip()) {
            new AlertDialog.Builder(this, R.style.BlueAlertDialog)
                    .setTitle(R.string.logout_title)
                    .setMessage(R.string.logout_message)
                    .setPositiveButton(R.string.ok_label, null)
                    .show();
        } else {
            SPHelper.deleteData(this, SPHelper.USER_SESSION_KEY);
            navigator.loginActivity();
        }
    }

    private void checkUpdate() {
        fpModel.fpApi.checkUpdate(new MyCallback<UpdateAppObject>() {
            @Override
            public void onSuccess(UpdateAppObject response) {
                if(response.version != null){
                    new UpdateHelper(RunwayMapActivity.this, response, null).askUpdateApp();
                }else{
                    new AlertDialog.Builder(RunwayMapActivity.this, R.style.BlueAlertDialog)
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
        }else if(id == DrawerMenuView.ADD_INSPECTION){
            navigator.addInspection();
        }else if(id == DrawerMenuView.GET_JOBS){
            getJobs();
        }else if(id == DrawerMenuView.JOB_INFO){

        }else if(id == DrawerMenuView.LOGOUT){
            logout();
        }else if(id == DrawerMenuView.EXIT){
            onExit();
        }
    }

    private void getJobs(){
        fpModel.fpApi.getJobs(new JobsRequest(SPHelper.getUserSession(this)), new MyCallback<List<JobObject>>() {
            @Override
            public void onSuccess(List<JobObject> response) {
                DBHelper.getInstance().insertMultiple(new JobsTable(), new JobsTable().getMultipleValues(response));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RunwayMapActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

}
