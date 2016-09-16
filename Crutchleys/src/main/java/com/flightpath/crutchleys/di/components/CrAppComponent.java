package com.flightpath.crutchleys.di.components;

import com.flightpath.crutchleys.di.modules.CrAppModule;
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
        modules = {CrAppModule.class}
)
public interface CrAppComponent extends ApplicationComponent{
    LocationHandler locationHandler();
    TripStatusHelper tripStatusHelper();

}
