package com.flightpathcore.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.flightpathcore.di.DICore;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DICore.diCore().withApp(this);
        initInstances();
    }

    public static boolean isDebug(Context context){
        String pName = context.getPackageName();
        if (pName != null && ( pName.endsWith(".debug") || pName.endsWith(".dev"))) {
            Log.d("DEBUG", "true");
            return true;
        } else {
            Log.d("DEBUG", "false");
            return false;
        }
    }

    protected abstract void initInstances();
    public abstract AppObject getAppObject();
    public abstract void getComponent();
    public abstract void logCrash(Throwable error);
}
