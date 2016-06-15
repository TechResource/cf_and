package com.flightpath.easidrive.di.components;

import com.flightpath.easidrive.activity.InspectionActivity;
import com.flightpath.easidrive.activity.LoginActivity;
import com.flightpath.easidrive.activity.MapActivity;
import com.flightpath.easidrive.activity.PrepareTripActivity;
import com.flightpath.easidrive.activity.SplashScreenActivity;
import com.flightpath.easidrive.di.modules.EDActivityModule;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@ActivityScope
@Component(
        dependencies = {EDAppComponent.class},
        modules = {EDActivityModule.class}
)
public interface EDActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(LoginActivity activity);
    void inject(MapActivity activity);
    void inject(PrepareTripActivity activity);
    void inject(InspectionActivity activity);
}
