package flightpath.com.donglemodule;

import android.app.Activity;
import android.content.Context;

import com.flightpathcore.utilities.SPHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-01.
 */
public class DonglePreferences {

    private static final String SP_NAME = SPHelper.SP_PREFIX + "dongle_preferences";
    private static final String DONGLE_PAIRED_DEVICE = "dongle_paired_device";

    public static String getPairedDevice(Context context) {
        return context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE)
                .getString(DONGLE_PAIRED_DEVICE, "");
    }

    public static void savePairedDevice(Context context, String pairedDevice) {
        context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE)
                .edit()
                .putString(DONGLE_PAIRED_DEVICE, pairedDevice)
                .apply();
    }

}
