package com.flightpath.easidrive.di.components;

import com.flightpath.easidrive.di.modules.EDAppModule;
import com.flightpathcore.di.components.AppScope;
import com.flightpathcore.di.components.ApplicationComponent;

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
        modules = {EDAppModule.class}
)
public interface EDAppComponent extends ApplicationComponent{
    LocationHandler locationHandler();
    TripStatusHelper tripStatusHelper();

}
