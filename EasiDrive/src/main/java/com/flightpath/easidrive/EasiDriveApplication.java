package com.flightpath.easidrive;

import android.content.Context;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.flightpath.easidrive.di.*;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.AppObject;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 15.06.2016.
 */
public class EasiDriveApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!BaseApplication.isDebug(this)) {
            Fabric.with(this, new Crashlytics());
        }
        AppCore.initInstance(getAppObject());
    }

    @Override
    protected void initInstances() {
        DBHelper.initInstance(this);
    }

    @Override
    public AppObject getAppObject() {
        DI.di().withApp(this);
        try {
            return new AppObject(getString(R.string.app_name), getString(R.string.api_version),
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + "",
                    "CLM");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can't get version name");
        }
    }

    @Override
    public void getComponent() {

    }

    @Override
    public void logCrash(Throwable error) {
        if (!isDebug(this)) {
            if (error != null)
                Crashlytics.logException(error);
        }
    }

    @Override
    public void logCrash(Throwable error, String extraInfo) {
        if (!isDebug(this)) {
            if (extraInfo != null)
                Crashlytics.log(extraInfo);
            if (error != null)
                Crashlytics.logException(error);
        }
    }

    public static boolean isCLM2(Context context) {
        String pName = context.getPackageName();
        if (pName != null && (pName.endsWith(".clm2"))) {
            return true;
        } else {
            return false;
        }
    }
}
