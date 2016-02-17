package com.flightpathcore.di;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.di.components.ApplicationComponent;
import com.flightpathcore.di.components.DaggerApplicationComponent;
import com.flightpathcore.di.modules.ApplicationModule;
import com.flightpathcore.network.SynchronizationHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
public class DICore {
    private static DICore diCore = new DICore();
    private Injections injection = new Injections();
    private ApplicationComponent applicationComponent;

    public static DICore diCore() {
        return diCore;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(app)).build();
    }

    public Injections injections(){
        return injection;
    }

    public class Injections{
        public void inject(SynchronizationHelper helper){
            applicationComponent.inject(helper);
        }

    }
}
