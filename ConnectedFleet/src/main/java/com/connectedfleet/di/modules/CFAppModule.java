package com.connectedfleet.di.modules;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.di.modules.ApplicationModule;

import dagger.Module;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class CFAppModule extends ApplicationModule {

    public CFAppModule(BaseApplication application) {
        super(application);
    }

}
