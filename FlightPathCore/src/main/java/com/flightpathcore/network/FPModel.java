package com.flightpathcore.network;

import android.content.Context;
import android.os.Build;

import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.ServerChooser;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class FPModel {

    public FPApi fpApi;
    private RestAdapter restAdapter;
    private UserObject userObject = null;

    public FPModel(Context context) {
        changeServerAddress(context);
    }

    public void changeServerAddress(Context context, String hostAddress) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(hostAddress)
                .setLogLevel(BaseApplication.isDebug(context) ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setConverter(new SuccessResponseObject())
                .setRequestInterceptor(request -> {
                    userObject = SPHelper.getUserSession(context);
                    request.addHeader("API_VERSION", AppCore.getInstance().getAppInfo().apiVersion);
                    if ((context != null && userObject != null && userObject.access != null) /*&& isTokenActual(SPA.getUserData(context))*/) { // TODO check token is actual
                        request.addHeader("TOKEN-ID", userObject.tokenId + "");
                        request.addHeader("ACCESS-TOKEN", userObject.access);
                    }
                    request.addHeader("APP_VERSION", AppCore.getInstance().getAppInfo().appVersion);
                    request.addHeader("APP_VERSION_CODE", AppCore.getInstance().getAppInfo().appVersionCode + "");

                    request.addHeader("DEVICE_PLATFORM", "android");
                    request.addHeader("DEVICE_OS", Build.VERSION.SDK_INT + "(" + Build.VERSION.RELEASE + ")");
                    request.addHeader("DEVICE_MODEL", Build.MANUFACTURER + " " + Build.MODEL);
                })
                .setClient(new OkClient(new OkHttpClient()))
                .build();
        fpApi = restAdapter.create(FPApi.class);
    }

    public void changeServerAddress(Context context) {
        changeServerAddress(context, ServerChooser.getHostAddress(context));
    }
}
