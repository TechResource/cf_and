package com.runway.di;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;
import com.runway.activity.InspectionActivity;
import com.runway.activity.LoginActivity;
import com.runway.activity.RunwayMapActivity;
import com.runway.activity.SplashScreenActivity;
import com.runway.di.components.DaggerRunwayActivityComponent;
import com.runway.di.components.DaggerRunwayAppComponent;
import com.runway.di.components.RunwayActivityComponent;
import com.runway.di.components.RunwayAppComponent;
import com.runway.di.modules.RunwayActivityModule;
import com.runway.di.modules.RunwayAppModule;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private RunwayAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerRunwayAppComponent.builder().runwayAppModule(new RunwayAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public RunwayAppComponent applicationComponent(){
        return applicationComponent;
    }

    public RunwayActivityComponent activityComponent(BaseActivity activity){
        RunwayActivityComponent component = DaggerRunwayActivityComponent.builder()
                .runwayAppComponent(applicationComponent())
                .runwayActivityModule(new RunwayActivityModule(activity))
                .build();
        return component;
    }


    public class Injections{

        public void inject(LoginActivity activity){
            activityComponent(activity).inject(activity);
        }

        public void inject(SplashScreenActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(RunwayMapActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(InspectionActivity activity) {
            activityComponent(activity).inject(activity);
        }
    }

}
