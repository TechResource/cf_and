package com.flightpath.gemini.di.components;

import com.flightpath.gemini.di.modules.GeminiAppModule;
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
        modules = {GeminiAppModule.class}
)
public interface GeminiAppComponent extends ApplicationComponent{
    LocationHandler locationHandler();
    TripStatusHelper tripStatusHelper();

}
