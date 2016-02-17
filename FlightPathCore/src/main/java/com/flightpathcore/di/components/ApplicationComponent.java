package com.flightpathcore.di.components;

import com.flightpathcore.di.modules.ApplicationModule;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.SynchronizationHelper;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@AppScope
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    FPModel fpModel();

    void inject(SynchronizationHelper helper);
}
