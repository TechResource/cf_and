package com.connectedfleet.activity;

import android.os.Bundle;

import com.connectedfleet.Navigator;
import com.connectedfleet.di.DI;
import com.flightpathcore.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
public class CFBaseActivity extends BaseActivity {

    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }

}
