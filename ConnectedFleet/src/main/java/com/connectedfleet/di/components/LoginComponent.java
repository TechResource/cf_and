package com.connectedfleet.di.components;

import com.connectedfleet.activity.LoginActivity;
import com.connectedfleet.di.modules.LoginModule;
import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.di.components.ApplicationComponent;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = LoginModule.class
)
public interface LoginComponent {
    void inject(LoginActivity activity);
}
