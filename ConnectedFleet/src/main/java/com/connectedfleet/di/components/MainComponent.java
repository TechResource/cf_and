package com.connectedfleet.di.components;

import com.connectedfleet.activity.MainActivity;
import com.connectedfleet.di.modules.MainModule;
import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.di.components.ApplicationComponent;

import dagger.Component;
import dagger.Module;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.di.MapComponent;
import flightpath.com.mapmodule.di.MapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = MainModule.class
)
public interface MainComponent {
    void inject(MainActivity activity);
}
