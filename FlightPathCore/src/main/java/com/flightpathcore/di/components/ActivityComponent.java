package com.flightpathcore.di.components;

import android.app.Activity;

import com.flightpathcore.di.modules.BaseActivityModule;

import dagger.Component;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = BaseActivityModule.class
)
public interface ActivityComponent {
}
