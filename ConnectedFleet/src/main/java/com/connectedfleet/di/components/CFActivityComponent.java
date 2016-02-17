package com.connectedfleet.di.components;

import com.connectedfleet.activity.ClosePeriodActivity;
import com.connectedfleet.activity.LoginActivity;
import com.connectedfleet.activity.MainActivity;
import com.connectedfleet.activity.PrepareTripActivity;
import com.connectedfleet.activity.SplashScreenActivity;
import com.connectedfleet.di.modules.CFActivityModule;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
@ActivityScope
@Component(
        dependencies = {CFAppComponent.class},
        modules = {CFActivityModule.class}
)
public interface CFActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(PrepareTripActivity activity);
    void inject(ClosePeriodActivity activity);
}
