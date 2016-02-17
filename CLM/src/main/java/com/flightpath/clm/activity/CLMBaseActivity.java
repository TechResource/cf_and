package com.flightpath.clm.activity;

import com.flightpath.clm.Navigator;
import com.flightpath.clm.di.DI;
import com.flightpathcore.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class CLMBaseActivity extends BaseActivity {

    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }
}
