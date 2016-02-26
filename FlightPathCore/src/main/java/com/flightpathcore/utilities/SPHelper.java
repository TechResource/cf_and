package com.flightpathcore.utilities;

import android.app.Activity;
import android.content.Context;

import com.flightpathcore.base.AppCore;
import com.flightpathcore.objects.UserObject;
import com.google.gson.Gson;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class SPHelper {
    public static final String SP_PREFIX = "com.fp.";
    public static final String USER_SESSION_KEY = "user_session";
    public static final String CURRENT_STATUS_KEY = "current_status";
    public static final String INSPECTION_STRUCTURE = "inspection_structure";
    public static final String SAVED_INSPECTION = "saved_inspection";
    public static final String SAVED_INSPECTION_EVENT_ID = "saved_inspection_event_id";

    public static void saveData(Context context, String key, String data) {
        context.getSharedPreferences(SP_PREFIX + getAppName(), Activity.MODE_PRIVATE).edit().putString(key, data).apply();
    }

    public static String getData(Context context, String key) {
        return context.getSharedPreferences(SP_PREFIX + getAppName(), Activity.MODE_PRIVATE).getString(key, "");
    }

    private static String getAppName() {
        if(AppCore.getInstance() != null && AppCore.getInstance().getAppInfo() != null && AppCore.getInstance().getAppInfo().appName != null){
            return AppCore.getInstance().getAppInfo().appName.toLowerCase().replace(" ", "_");
        }else{
            throw new NullPointerException("AppCore -> appInfo missing");
        }
    }

    public static UserObject getUserSession(Context context){
        String sessionContent = getData(context, USER_SESSION_KEY);
        if(sessionContent != null && !sessionContent.isEmpty()) {
            return new Gson().fromJson(sessionContent, UserObject.class);
        }else{
            return null;
        }
    }

    public static void deleteData(Context context, String key) {
        context.getSharedPreferences(SP_PREFIX + getAppName(), Activity.MODE_PRIVATE)
                .edit()
                .remove(key)
                .commit();
    }
}