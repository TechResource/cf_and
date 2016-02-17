package com.connectedfleet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.connectedfleet.R;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.utilities.SPHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

import flightpath.com.loginmodule.SplashScreenFragment;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
@EActivity(R.layout.splash_screen_activity)
public class SplashScreenActivity extends CFBaseActivity implements SplashScreenFragment.SplashScreenCallback{

    @FragmentByTag
    protected SplashScreenFragment splashScreenFragment;

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
