package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-09.
 */
@EViewGroup(resName = "drawer_menu_view")
public class DrawerMenuView extends LinearLayout implements View.OnClickListener {

    public static int UPDATE_APP = R.id.updateApp;
    public final static int STATUS = R.id.status;
    public final static int ADD_INSPECTION = R.id.addInspection;
    public final static int GET_JOBS = R.id.getJobs;
    public final static int JOB_INFO = R.id.jobInfo;
    public final static int LOGOUT = R.id.logout;
    public final static int EXIT = R.id.exit;
    public final static int CLOSE_PERIOD = R.id.closePeriod;
    public final static int CLEAR_DB = R.id.cleanDB;
    public final static int STORE_DB = R.id.storeDB;
    public final static int SYNC_NOW = R.id.sendNow;
    public final static int DISPOSAL_INSPECTION = R.id.disposalInspection;
    public final static int JOB_LIST = R.id.jobList;

    @ViewById
    protected TextView driver;
    @ViewById
    protected Button updateApp, status, addInspection, getJobs, jobInfo, logout, exit, closePeriod, cleanDB, sendNow, storeDB, disposalInspection, jobList;
    private MenuCallbacks callbacks;

    public DrawerMenuView(Context context) {
        super(context);
    }

    public DrawerMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawerMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCallbacks(MenuCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void setupMenu(Integer... ids) {
        for (Integer i : ids) {
            if (i != null)
                findViewById(i).setVisibility(VISIBLE);
        }
        if (BaseApplication.isDebug(getContext())) {
            cleanDB.setVisibility(VISIBLE);
            sendNow.setVisibility(VISIBLE);
            storeDB.setVisibility(VISIBLE);
        }
    }

    @AfterViews
    protected void init() {
        Utilities.setOswaldTypeface(getContext().getAssets(), driver, updateApp, status, addInspection,
                getJobs, jobInfo, logout, exit, closePeriod, cleanDB, sendNow, storeDB, disposalInspection, jobList);
        String name = null;
        if (!isInEditMode()) {
            if (DBHelper.getInstance().getLast(new DriverTable()) != null) {
                name = ((UserObject) DBHelper.getInstance().getLast(new DriverTable())).name;
            }
        }
        driver.setText(name != null ? name : "");
        updateApp.setOnClickListener(this);
        status.setOnClickListener(this);
        addInspection.setOnClickListener(this);
        getJobs.setOnClickListener(this);
        jobInfo.setOnClickListener(this);
        logout.setOnClickListener(this);
        exit.setOnClickListener(this);
        closePeriod.setOnClickListener(this);
        cleanDB.setOnClickListener(this);
        sendNow.setOnClickListener(this);
        storeDB.setOnClickListener(this);
        disposalInspection.setOnClickListener(this);
        jobList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (callbacks != null) {
            if (v == cleanDB)
                DBHelper.getInstance().clearDB();
            if (v == sendNow)
                SynchronizationHelper.getInstance().sendNow(getContext());
            if (v == storeDB)
                Utilities.storeDatabase(getContext().getApplicationContext().getPackageName());

            callbacks.onMenuItemSelected(v.getId());
        }
    }


    public interface MenuCallbacks {
        void onMenuItemSelected(int id);
    }
}
