package com.flightpathcore.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.flightpathcore.objects.JobObject;
import com.flightpathcore.utilities.Utilities;


/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.overrideFonts(this, findViewById(android.R.id.content));
    }

    public interface JobSelectCallback{
        void onJobSelected(JobObject jobObject);
        void onJobUnselect();
        Long getSelectedJobId();
    }
}
