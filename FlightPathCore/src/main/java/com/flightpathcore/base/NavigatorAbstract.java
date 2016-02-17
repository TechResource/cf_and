package com.flightpathcore.base;

import android.app.Activity;

import com.flightpathcore.di.components.ActivityScope;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public abstract class NavigatorAbstract {

    private Activity activity;


    public abstract void loginActivity();
    public abstract void loginSuccessfully();
    public abstract void prepareTrip();
    public abstract void splashScreen();

    public static NavigatorAbstract buildEmpty(){
        return new NavigatorAbstract() {

            @Override
            public void splashScreen() {

            }

            @Override
            public void loginActivity() {

            }

            @Override
            public void loginSuccessfully() {

            }

            @Override
            public void prepareTrip() {

            }
        };
    }
}
