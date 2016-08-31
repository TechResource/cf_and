package com.connectedfleet.di;

import com.connectedfleet.activity.ClosePeriodActivity;
import com.connectedfleet.activity.LoginActivity;
import com.connectedfleet.activity.MainActivity;
import com.connectedfleet.activity.PrepareTripActivity;
import com.connectedfleet.activity.SplashScreenActivity;
import com.connectedfleet.di.components.CFActivityComponent;
import com.connectedfleet.di.components.CFAppComponent;
import com.connectedfleet.di.components.DaggerCFActivityComponent;
import com.connectedfleet.di.components.DaggerCFAppComponent;
import com.connectedfleet.di.modules.CFActivityModule;
import com.connectedfleet.di.modules.CFAppModule;
import com.connectedfleet.fragments.ClosePeriodStep2Fragment;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private CFAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerCFAppComponent.builder().cFAppModule(new CFAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public CFAppComponent applicationComponent(){
        return applicationComponent;
    }

    public CFActivityComponent activityComponent(BaseActivity activity){
        CFActivityComponent component = DaggerCFActivityComponent.builder()
                .cFAppComponent(applicationComponent())
                .cFActivityModule(new CFActivityModule(activity))
                .build();
        return component;
    }


    public class Injections{

        public void inject(LoginActivity activity){
//            DaggerLoginComponent.builder()
//                    .applicationComponent(applicationComponent())
//                    .loginModule(new LoginModule(activity))
//                    .build().inject(activity);
            activityComponent(activity).inject(activity);
        }

        public void inject(MainActivity activity){
//            MainComponent component = DaggerMainComponent.builder()
//                    .mainModule(new MainModule(activity))
//                    .build();
//            component.inject(activity);
            activityComponent(activity).inject(activity);
        }

        public void inject(PrepareTripActivity activity){
            activityComponent(activity).inject(activity);
        }

        public void inject(SplashScreenActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(ClosePeriodActivity activity) {
            activityComponent(activity).inject(activity);
        }

        public void inject(ClosePeriodStep2Fragment fragment) {
            applicationComponent().inject(fragment);
        }
    }
}
