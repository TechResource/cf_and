package com.runway.di.modules;

import android.app.Activity;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.di.modules.BaseActivityModule;
import com.runway.Navigator;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@Module
public class RunwayActivityModule extends BaseActivityModule {

    private Activity activity;

    public RunwayActivityModule(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public NavigatorAbstract navigator() {
        return new Navigator(activity);
    }
}
