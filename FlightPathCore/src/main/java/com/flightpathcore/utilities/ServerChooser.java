package com.flightpathcore.utilities;

import android.content.Context;

import com.flightpathcore.R;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.utilities.SPHelper;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-06-17.
 */
public class ServerChooser {

    private static String[] allHostAddresses = null;
    private static final String HOST_ADDRESS_PREF = "host_address";

    public static void setHostAddress(Context context, String hostAddress){
        SPHelper.saveData(context, HOST_ADDRESS_PREF, hostAddress);
    }

    public static String getHostAddress(Context context){
        String savedHost = SPHelper.getData(context, HOST_ADDRESS_PREF);
        if(savedHost == null || savedHost.isEmpty()){
            savedHost = getAllHostAddresses(context)[0];
        }
        return savedHost;
    }

    public static String[] getAllHostAddresses(Context context){
        if(allHostAddresses == null){
            allHostAddresses = context.getResources().getStringArray(R.array.host_addresses);
        }
        return allHostAddresses;
    }

}
