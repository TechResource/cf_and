package com.connectedfleet.di.components;

import com.connectedfleet.di.modules.CFAppModule;
import com.connectedfleet.fragments.ClosePeriodStep2Fragment;
import com.flightpathcore.di.components.AppScope;
import com.flightpathcore.di.components.ApplicationComponent;

import dagger.Component;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.TripStatusHelper;
import flightpath.com.mapmodule.di.MapComponent;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@AppScope
@Component(
        dependencies = {MapComponent.class},
        modules = {CFAppModule.class}
)
public interface CFAppComponent extends ApplicationComponent {
    LocationHandler locationHandler();

    TripStatusHelper tripStatusHelper();

    void inject(ClosePeriodStep2Fragment fragment);
}
