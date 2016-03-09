package com.flightpath.gemini.activity;

import com.flightpath.gemini.Navigator;
import com.flightpath.gemini.di.DI;
import com.flightpathcore.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class GeminiBaseActivity extends BaseActivity {

    @Inject
    protected Navigator navigator;

    protected DI.Injections di() {
        return DI.di().injections();
    }
}
