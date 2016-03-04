package com.flightpathcore.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.database.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
public class Utilities {
    public final static String DIRECTORY_NAME = "FlightPath";
    private final static SimpleDateFormat sdf = new SimpleDateFormat();

    public static String getUpdateApkPath() {
        return getBaseDirectoryPath() + "update.apk";
    }

    public static String getUtcDateTime(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }

    /**
     * @return path+"/";
     */
    private static String getBaseDirectoryPath() {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_NAME);
        if(!f.exists()){
            f.mkdir();
        }
        return Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_NAME + File.separator;
    }

    public static void hideSoftKeyboard(final Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if ((inputMethodManager != null) && (activity.getCurrentFocus() != null)) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String formatNumber(double number) {
        return formatNumber(number, 5);
    }

    public static String formatNumber(double number, int digitsFraction){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(digitsFraction);
        numberFormat.setMinimumFractionDigits(digitsFraction);
        return numberFormat.format(number);
    }

    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    public static String getCurrentDateFormatted() {
        return sdf.format(new Date(getTimestamp()));
    }

    public static String getDateFromTimestamp(long timestmap){
        return sdf.format(new Date(timestmap));
    }

    private static Typeface oswaldTypeface = null;

    public static void setOswaldTypeface(AssetManager assets, TextView... views) {
        if (oswaldTypeface == null) {
            oswaldTypeface = Typeface.createFromAsset(assets, "Oswald-Bold.ttf");
        }
        for (TextView t : views) {
            t.setTypeface(oswaldTypeface);
        }
    }

    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;

                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                if (((TextView) v).getTypeface() != null ) {
                    if(((TextView) v).getTypeface() != oswaldTypeface) {
                        if (((TextView) v).getTypeface().isBold()) {
                            ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Bold.ttf"));
                        } else if (((TextView) v).getTypeface().isItalic()) {
                            ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"));
                        } else {
                            ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf"));
                        }
                    }
                } else {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf"));
                }
            } else if (v instanceof EditText) {
                if(((EditText) v).getTypeface() == null || ((EditText) v).getTypeface() != oswaldTypeface)
                    ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeDatabase(String appPackageName) {
        File DbFile = new
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + "/" + DBHelper.DATABASE_NAME);
        try {
            DbFile.createNewFile();
            InputStream is = new FileInputStream("/data/data/" + appPackageName + "/databases/" +
                    DBHelper.DATABASE_NAME);
            FileOutputStream fos = new FileOutputStream(DbFile);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            // Close the streams
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void styleAlertDialog(AlertDialog dialog){
        styleAlertDialog(dialog, null);
    }

    public static void styleAlertDialog(AlertDialog dialog, Float textSize) {
        int textViewId = dialog.getContext().getResources().getIdentifier("app:id/alertTitle", null, null);
        if (textViewId != 0) {
            TextView tv = (TextView) dialog.findViewById(textViewId);
            if(tv != null) {
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            }
        }

        Button posBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(posBtn != null) {
            if(textSize != null) {
                posBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            posBtn.setTextColor(dialog.getContext().getResources().getColor(R.color.blue_bg));
            posBtn.setTypeface(posBtn.getTypeface(), Typeface.BOLD);
        }
        Button negBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if(negBtn != null) {
            if(textSize != null) {
                negBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            negBtn.setTextColor(dialog.getContext().getResources().getColor(R.color.gray_dark));
            negBtn.setTypeface(negBtn.getTypeface(), Typeface.BOLD);
        }
    }

}
