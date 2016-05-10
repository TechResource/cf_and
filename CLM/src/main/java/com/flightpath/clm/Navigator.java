package com.flightpath.clm;

import android.app.Activity;

import com.flightpath.clm.activity.DisposalInspectionActivity_;
import com.flightpath.clm.activity.InspectionActivity_;
import com.flightpath.clm.activity.LoginActivity_;
import com.flightpath.clm.activity.MapActivity_;
import com.flightpath.clm.activity.PrepareTripActivity_;
import com.flightpath.clm.activity.SplashScreenActivity_;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
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
        MapActivity_.intent(activity).start();
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_bottom_fade, R.anim.stay);
    }

    @Override
    public void prepareTrip() {
        PrepareTripActivity_.intent(activity).start();
    }

    @Override
    public void splashScreen() {
        SplashScreenActivity_.intent(activity).start();
        activity.finish();
    }

    public void addInspection(){
        InspectionActivity_.intent(activity).start();
    }

    public void closePeriod() {
    }

    public void addDisposalInspection() {
        DisposalInspectionActivity_.intent(activity).start();
    }
}
