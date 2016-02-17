package com.runway;

import android.app.Application;
import android.content.pm.PackageManager;

import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.AppObject;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.network.SynchronizationHelper;
import com.runway.di.DI;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class RunwayApplication extends BaseApplication {
    @Override
    protected void initInstances() {
        AppCore.initInstance(getAppObject());
        DBHelper.initInstance(this);
    }

    @Override
    public AppObject getAppObject() {
        DI.di().withApp(this);
        try {
            return new AppObject(getString(R.string.app_name), getString(R.string.api_version),
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+"");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can't get version name");
        }
    }

    @Override
    public void getComponent() {

    }
}
