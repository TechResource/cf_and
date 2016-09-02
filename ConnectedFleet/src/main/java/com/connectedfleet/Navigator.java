package com.connectedfleet;

import android.app.Activity;

import com.connectedfleet.activity.ClosePeriodActivity_;
import com.connectedfleet.activity.LoginActivity_;
import com.connectedfleet.activity.MainActivity_;
import com.connectedfleet.activity.PrepareTripActivity_;
import com.connectedfleet.activity.ReturningVehicleActivity_;
import com.connectedfleet.activity.SplashScreenActivity_;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
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
        MainActivity_.intent(activity).start();
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_bottom_fade, R.anim.stay);
    }

    @Override
    public void prepareTrip() {
        PrepareTripActivity_.intent(activity).start();
    }

    public void closePeriod() {
        ClosePeriodActivity_.intent(activity).start();
    }

    @Override
    public void splashScreen() {
        SplashScreenActivity_.intent(activity).start();
        activity.finish();
    }

    public void returningVehicle() {
        ReturningVehicleActivity_.intent(activity).start();
    }
}
