package com.flightpathcore.di.modules;

import android.app.Activity;

import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public abstract class ActivityModule<T extends BaseActivity> {
    private T activity;

    public ActivityModule(T activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public Activity activity() {
        return activity;
    }

}
