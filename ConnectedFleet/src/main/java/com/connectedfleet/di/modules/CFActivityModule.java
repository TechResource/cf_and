package com.connectedfleet.di.modules;

import android.app.Activity;

import com.connectedfleet.Navigator;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;
import com.flightpathcore.di.modules.BaseActivityModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
@Module
public class CFActivityModule extends BaseActivityModule {

    private Activity activity;

    public CFActivityModule(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    public NavigatorAbstract navigator() {
        return new Navigator(activity);
    }

}
