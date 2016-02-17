package com.flightpath.clm.di.components;

import com.flightpath.clm.activity.LoginActivity;
import com.flightpath.clm.activity.SplashScreenActivity;
import com.flightpath.clm.di.modules.CLMAppModule;
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
        modules = {CLMAppModule.class}
)
public interface CLMAppComponent extends ApplicationComponent{
    LocationHandler locationHandler();
    TripStatusHelper tripStatusHelper();

}
