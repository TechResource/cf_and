package com.runway;

import android.app.Activity;

import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;
import com.runway.activity.InspectionActivity_;
import com.runway.activity.LoginActivity_;
import com.runway.activity.PrepareTripActivity_;
import com.runway.activity.RunwayMapActivity_;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class Navigator extends NavigatorAbstract {

    private Activity activity;

    @Inject
    public Navigator(@ActivityScope Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loginActivity() {
        LoginActivity_.intent(activity).start();
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_bottom_fade, R.anim.stay);
    }

    @Override
    public void loginSuccessfully() {
        RunwayMapActivity_.intent(activity).start();
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_bottom_fade, R.anim.stay);
    }

    @Override
    public void prepareTrip() {
        PrepareTripActivity_.intent(activity).start();
    }

    @Override
    public void splashScreen() {

    }

    public void addInspection(){
        InspectionActivity_.intent(activity).start();
    }
}
