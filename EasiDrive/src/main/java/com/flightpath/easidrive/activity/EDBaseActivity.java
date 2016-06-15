package com.flightpath.easidrive.activity;

import com.flightpath.easidrive.Navigator;
import com.flightpathcore.base.BaseActivity;
import com.flightpath.easidrive.di.*;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 15.06.2016.
 */
public class EDBaseActivity extends BaseActivity {
    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }
}
