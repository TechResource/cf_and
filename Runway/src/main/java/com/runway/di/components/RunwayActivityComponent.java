package com.runway.di.components;

import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;
import com.runway.activity.InspectionActivity;
import com.runway.activity.LoginActivity;
import com.runway.activity.RunwayMapActivity;
import com.runway.activity.SplashScreenActivity;
import com.runway.di.modules.RunwayActivityModule;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@ActivityScope
@Component(
        dependencies = {RunwayAppComponent.class},
        modules = {RunwayActivityModule.class}
)
public interface RunwayActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(LoginActivity activity);
    void inject(RunwayMapActivity activity);
    void inject(InspectionActivity activity);
}
