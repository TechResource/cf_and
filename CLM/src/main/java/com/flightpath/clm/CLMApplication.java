package com.flightpath.clm;

import android.content.Context;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.flightpath.clm.di.DI;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.AppObject;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.network.SynchronizationHelper;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class CLMApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if(!BaseApplication.isDebug(this)) {
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
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+"",
                    "CLM");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can't get version name");
        }
    }

    @Override
    public void getComponent() {

    }

    public static boolean isCLM2(Context context){
        String pName = context.getPackageName();
        if (pName != null && ( pName.endsWith(".clm2") )) {
            return true;
        } else {
            return false;
        }
    }
}
