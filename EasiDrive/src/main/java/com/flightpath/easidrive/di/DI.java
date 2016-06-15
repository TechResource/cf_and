package com.flightpath.easidrive.di;

import com.flightpath.easidrive.activity.InspectionActivity;
import com.flightpath.easidrive.activity.LoginActivity;
import com.flightpath.easidrive.activity.MapActivity;
import com.flightpath.easidrive.activity.PrepareTripActivity;
import com.flightpath.easidrive.activity.SplashScreenActivity;
import com.flightpath.easidrive.di.components.DaggerEDActivityComponent;
import com.flightpath.easidrive.di.components.DaggerEDAppComponent;
import com.flightpath.easidrive.di.components.EDActivityComponent;
import com.flightpath.easidrive.di.components.EDAppComponent;
import com.flightpath.easidrive.di.modules.EDActivityModule;
import com.flightpath.easidrive.di.modules.EDAppModule;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.base.NavigatorAbstract;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private EDAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerEDAppComponent.builder().eDAppModule(new EDAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public EDAppComponent applicationComponent(){
        return applicationComponent;
    }

    public EDActivityComponent activityComponent(BaseActivity activity){
        EDActivityComponent component = DaggerEDActivityComponent.builder()
                .eDAppComponent(applicationComponent())
                .eDActivityModule(new EDActivityModule(activity))
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
