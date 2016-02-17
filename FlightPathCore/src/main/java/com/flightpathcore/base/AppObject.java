package com.flightpathcore.base;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class AppObject {
    public String appName;
    public String apiVersion;
    public String appVersion;
    public String appVersionCode;

    public AppObject(){

    }

    public AppObject(String appName,String apiVersion, String appVersion, String appVersionCode) {
        this.appName = appName;
        this.apiVersion = apiVersion;
        this.appVersion = appVersion;
        this.appVersionCode = appVersionCode;
    }
}
