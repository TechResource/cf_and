package com.flightpath.gemini.di.modules;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.di.modules.ApplicationModule;

import dagger.Module;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
@Module
public class GeminiAppModule extends ApplicationModule {

    public GeminiAppModule(BaseApplication application) {
        super(application);
    }
}
