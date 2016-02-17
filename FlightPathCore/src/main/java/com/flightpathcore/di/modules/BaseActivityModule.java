package com.flightpathcore.di.modules;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.di.components.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class BaseActivityModule extends ActivityModule<BaseActivity> {

    public BaseActivityModule(BaseActivity activity) {
        super(activity);
    }

}
