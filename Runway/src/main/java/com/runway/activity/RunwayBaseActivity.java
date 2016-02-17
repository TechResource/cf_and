package com.runway.activity;

import com.flightpathcore.base.BaseActivity;
import com.runway.Navigator;
import com.runway.di.DI;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class RunwayBaseActivity extends BaseActivity {
    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }
}
