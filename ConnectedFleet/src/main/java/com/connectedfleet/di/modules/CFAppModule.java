package com.connectedfleet.di.modules;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.di.modules.ApplicationModule;

import dagger.Module;
import dagger.Provides;
import flightpath.com.donglemodule.DongleDataHelper;
import flightpath.com.donglemodule.di.DIDongleModule;
import flightpath.com.donglemodule.di.DongleModule;
import flightpath.com.mapmodule.di.MapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class CFAppModule extends ApplicationModule {

    public CFAppModule(BaseApplication application) {
        super(application);
    }

}
