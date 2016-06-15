package com.flightpath.easidrive.di.modules;

import android.app.Activity;

import com.flightpath.easidrive.Navigator;
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
public class EDActivityModule extends BaseActivityModule {

    private Activity activity;

    public EDActivityModule(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public NavigatorAbstract navigator() {
        return new Navigator(activity);
    }
}
