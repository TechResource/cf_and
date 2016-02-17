package com.connectedfleet.di.modules;

import com.connectedfleet.activity.LoginActivity;
import com.flightpathcore.di.modules.ActivityModule;

import dagger.Module;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class LoginModule extends ActivityModule<LoginActivity> {

    public LoginModule(LoginActivity activity) {
        super(activity);
    }

}
