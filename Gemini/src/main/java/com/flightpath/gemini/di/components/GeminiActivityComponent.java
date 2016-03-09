package com.flightpath.gemini.di.components;

import com.flightpath.gemini.activity.InspectionActivity;
import com.flightpath.gemini.activity.LoginActivity;
import com.flightpath.gemini.activity.MapActivity;
import com.flightpath.gemini.activity.PrepareTripActivity;
import com.flightpath.gemini.activity.SplashScreenActivity;
import com.flightpath.gemini.di.modules.GeminiActivityModule;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@ActivityScope
@Component(
        dependencies = {GeminiAppComponent.class},
        modules = {GeminiActivityModule.class}
)
public interface GeminiActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(LoginActivity activity);
    void inject(MapActivity activity);
    void inject(InspectionActivity activity);
    void inject(PrepareTripActivity activity);
}
