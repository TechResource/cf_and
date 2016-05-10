package com.flightpath.clm.di.components;

import com.flightpath.clm.activity.DisposalInspectionActivity;
import com.flightpath.clm.activity.InspectionActivity;
import com.flightpath.clm.activity.LoginActivity;
import com.flightpath.clm.activity.MapActivity;
import com.flightpath.clm.activity.PrepareTripActivity;
import com.flightpath.clm.activity.SplashScreenActivity;
import com.flightpath.clm.di.modules.CLMActivityModule;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityComponent;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@ActivityScope
@Component(
        dependencies = {CLMAppComponent.class},
        modules = {CLMActivityModule.class}
)
public interface CLMActivityComponent extends ActivityComponent {
    NavigatorAbstract navigator();

    void inject(SplashScreenActivity activity);
    void inject(LoginActivity activity);
    void inject(MapActivity activity);
    void inject(InspectionActivity activity);
    void inject(PrepareTripActivity activity);
    void inject(DisposalInspectionActivity activity);
}
