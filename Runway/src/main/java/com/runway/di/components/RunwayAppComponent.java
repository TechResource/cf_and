package com.runway.di.components;

import com.flightpathcore.di.components.AppScope;
import com.flightpathcore.di.components.ApplicationComponent;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.SynchronizationHelper;
import com.runway.di.modules.RunwayAppModule;

import dagger.Component;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.TripStatusHelper;
import flightpath.com.mapmodule.di.MapComponent;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@AppScope
@Component(
        dependencies = {MapComponent.class},
        modules = {RunwayAppModule.class}
)
public interface RunwayAppComponent extends ApplicationComponent{
    LocationHandler locationHandler();
    TripStatusHelper tripStatusHelper();
}
