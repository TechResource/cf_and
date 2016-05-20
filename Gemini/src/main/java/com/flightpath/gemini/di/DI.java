package com.flightpath.gemini.di;

import com.flightpath.gemini.activity.InspectionActivity;
import com.flightpath.gemini.activity.JobListActivity;
import com.flightpath.gemini.activity.LoginActivity;
import com.flightpath.gemini.activity.MapActivity;
import com.flightpath.gemini.activity.PrepareTripActivity;
import com.flightpath.gemini.activity.SplashScreenActivity;
import com.flightpath.gemini.di.components.DaggerGeminiActivityComponent;
import com.flightpath.gemini.di.components.DaggerGeminiAppComponent;
import com.flightpath.gemini.di.components.GeminiActivityComponent;
import com.flightpath.gemini.di.components.GeminiAppComponent;
import com.flightpath.gemini.di.modules.GeminiActivityModule;
import com.flightpath.gemini.di.modules.GeminiAppModule;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.base.BaseApplication;

import flightpath.com.mapmodule.di.DIMapModule;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */
public class DI {

    private static DI di = new DI();
    public Injections injection = new Injections();
    private GeminiAppComponent applicationComponent;

    public static DI di() {
        return di;
    }

    public Injections injections(){
        return injection;
    }

    public void withApp(BaseApplication app){
        applicationComponent = DaggerGeminiAppComponent.builder().geminiAppModule(new GeminiAppModule(app)).mapComponent(DIMapModule.diMapModule().mapComponent).build();
    }

    public GeminiAppComponent applicationComponent(){
        return applicationComponent;
    }

    public GeminiActivityComponent activityComponent(BaseActivity activity){
        GeminiActivityComponent component = DaggerGeminiActivityComponent.builder()
                .geminiAppComponent(applicationComponent())
                .geminiActivityModule(new GeminiActivityModule(activity))
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

        public void inject(JobListActivity activity) {
            activityComponent(activity).inject(activity);
        }
    }
}
