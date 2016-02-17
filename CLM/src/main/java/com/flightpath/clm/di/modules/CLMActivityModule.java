package com.flightpath.clm.di.modules;

import android.app.Activity;

import com.flightpath.clm.Navigator;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.di.modules.BaseActivityModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@Module
public class CLMActivityModule extends BaseActivityModule {

    private Activity activity;

    public CLMActivityModule(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public NavigatorAbstract navigator() {
        return new Navigator(activity);
    }
}
