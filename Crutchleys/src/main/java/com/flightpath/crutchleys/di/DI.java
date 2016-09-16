package com.flightpath.crutchleys.di;

import com.flightpath.crutchleys.activity.SplashScreenActivity;
import com.flightpath.crutchleys.activity.InspectionActivity;
import com.flightpath.crutchleys.activity.LoginActivity;
import com.flightpath.crutchleys.activity.MapActivity;
import com.flightpath.crutchleys.activity.PrepareTripActivity;
import com.flightpath.crutchleys.activity.SplashScreenActivity;
import com.flightpath.crutchleys.di.components.DaggerCrActivityComponent;
import com.flightpath.crutchleys.di.components.DaggerCrAppComponent;
import com.flightpath.crutchleys.di.components.CrActivityComponent;
import com.flightpath.crutchleys.di.components.CrAppComponent;
import com.flightpath.crutchleys.di.modules.CrActivityModule;
import com.flightpath.crutchleys.di.modules.CrAppModule;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private CrAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerCrAppComponent.builder().crAppModule(new CrAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public CrAppComponent applicationComponent(){
        return applicationComponent;
    }

    public CrActivityComponent activityComponent(BaseActivity activity){
        CrActivityComponent component = DaggerCrActivityComponent.builder()
                .crAppComponent(applicationComponent())
                .crActivityModule(new CrActivityModule(activity))
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
