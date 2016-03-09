package com.flightpath.gemini.activity;

import android.os.Bundle;

import com.flightpath.gemini.Navigator;
import com.flightpath.gemini.R;
import com.flightpathcore.utilities.SPHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import javax.inject.Inject;

import flightpath.com.loginmodule.SplashScreenFragment;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.splash_screen_activity)
public class SplashScreenActivity extends GeminiBaseActivity implements SplashScreenFragment.SplashScreenCallback{

    @FragmentByTag
    protected SplashScreenFragment splashScreenFragment;
    @Inject
    protected Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
        if (SPHelper.getUserSession(SplashScreenActivity.this) != null) {
            navigator.loginSuccessfully();
        } else {
            splashScreenFragment.showPager(this);
        }
    }

    @Override
    public void onLoginBtnClick() {
        navigator.loginActivity();
    }
}
