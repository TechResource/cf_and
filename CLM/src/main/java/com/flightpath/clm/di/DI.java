package com.flightpath.clm.di;

import com.flightpath.clm.activity.InspectionActivity;
import com.flightpath.clm.activity.LoginActivity;
import com.flightpath.clm.activity.MapActivity;
import com.flightpath.clm.activity.PrepareTripActivity;
import com.flightpath.clm.activity.SplashScreenActivity;
import com.flightpath.clm.di.components.CLMActivityComponent;
import com.flightpath.clm.di.components.CLMAppComponent;
import com.flightpath.clm.di.components.DaggerCLMActivityComponent;
import com.flightpath.clm.di.components.DaggerCLMAppComponent;
import com.flightpath.clm.di.modules.CLMActivityModule;
import com.flightpath.clm.di.modules.CLMAppModule;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private CLMAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerCLMAppComponent.builder().cLMAppModule(new CLMAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public CLMAppComponent applicationComponent(){
        return applicationComponent;
    }

    public CLMActivityComponent activityComponent(BaseActivity activity){
        CLMActivityComponent component = DaggerCLMActivityComponent.builder()
                .cLMAppComponent(applicationComponent())
                .cLMActivityModule(new CLMActivityModule(activity))
                .build();
        return component;
    }


    public class Injections{

        public void inject(SplashScreenActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(LoginActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(MapActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(InspectionActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(PrepareTripActivity activity) {
            activityComponent(activity).inject(activity);
        }
    }
}
