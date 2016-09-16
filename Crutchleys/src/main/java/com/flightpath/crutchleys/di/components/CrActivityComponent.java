package com.flightpath.crutchleys.di.components;

import com.flightpath.crutchleys.activity.InspectionActivity;
import com.flightpath.crutchleys.activity.LoginActivity;
import com.flightpath.crutchleys.activity.MapActivity;
import com.flightpath.crutchleys.activity.PrepareTripActivity;
import com.flightpath.crutchleys.activity.SplashScreenActivity;
import com.flightpath.crutchleys.di.modules.CrActivityModule;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@ActivityScope
@Component(
        dependencies = {CrAppComponent.class},
        modules = {CrActivityModule.class}
)
public interface CrActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(LoginActivity activity);
    void inject(MapActivity activity);
    void inject(PrepareTripActivity activity);
    void inject(InspectionActivity activity);
}
