package com.flightpathcore.base;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class AppCore {
    private static AppCore instance = null;
    private AppObject appObject;

    public static AppCore getInstance(){
        return instance;
    }

    public static void initInstance(AppObject appObject){
        instance = new AppCore(appObject);
    }

    private AppCore(AppObject appObject){
        this.appObject = appObject;
    }

    public AppObject getAppInfo(){
        return appObject;
    }

}
