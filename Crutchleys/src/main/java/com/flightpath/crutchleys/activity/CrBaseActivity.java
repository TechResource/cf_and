package com.flightpath.crutchleys.activity;

import com.flightpath.crutchleys.Navigator;
import com.flightpath.crutchleys.di.DI;
import com.flightpathcore.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 15.06.2016.
 */
public class CrBaseActivity extends BaseActivity {
    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }
}
