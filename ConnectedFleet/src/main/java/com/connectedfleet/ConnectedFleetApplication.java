package com.connectedfleet;

import android.content.pm.PackageManager;

import com.connectedfleet.di.DI;
import com.crashlytics.android.Crashlytics;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.AppObject;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.network.SynchronizationHelper;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.Crash;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class ConnectedFleetApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if(!BaseApplication.isDebug(this)) {
            Fabric.with(this, new Crashlytics());
        }
        DI.di().withApp(this);
    }

    @Override
    public AppObject getAppObject() {

        try {
            return new AppObject(getString(R.string.app_name), getString(R.string.api_version),
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+"", "CF");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can't get version name");
        }
    }

    @Override
    public void logCrash(Throwable error) {
        if(!isDebug(this)){
            if(error != null)
                Crashlytics.logException(error);
        }
    }

    @Override
    public void logCrash(Throwable error, String extraInfo) {
        if(!isDebug(this)){
            if(extraInfo != null)
                Crashlytics.log(extraInfo);
            if(error != null)
                Crashlytics.logException(error);
        }
    }

    @Override
    protected void initInstances() {
        AppCore.initInstance(getAppObject());
        DBHelper.initInstance(this);
    }

    @Override
    public void getComponent() {

    }
}
