package com.flightpathcore.di.modules;

import android.content.Context;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.di.components.AppScope;
import com.flightpathcore.network.FPModel;

import org.androidannotations.annotations.App;

import java.nio.channels.FileChannel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class ApplicationModule {
    private BaseApplication application;
    private Context context;

    public ApplicationModule(BaseApplication application){
        this.application = application;
        this.context = application;
    }

    @Provides
    @AppScope
    public FPModel fpModel(){
        return new FPModel(context);
    }

}
