package flightpath.com.mapmodule.di;

import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.di.components.ApplicationComponent;
import com.flightpathcore.di.components.DaggerApplicationComponent;
import com.flightpathcore.di.modules.ApplicationModule;

import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.TripStartStopWidget;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
public class DIMapModule {
    private static DIMapModule diMapModule = new DIMapModule();
    private Injections injection = new Injections();
    public MapComponent mapComponent;

    public static DIMapModule diMapModule() {
        return diMapModule;
    }

    public DIMapModule(){
        mapComponent = DaggerMapComponent.builder().mapModule(new MapModule()).build();
    }

    public void setTestComponent(TestMapComponent testMapComponent){
        this.mapComponent = testMapComponent;
    }

    public Injections injections(){
        return injection;
    }

    public class Injections{
        public void inject(MapFragment mapFragment){
            mapComponent.inject(mapFragment);
        }

        public void inject(TripStartStopWidget widget){
            mapComponent.inject(widget);
        }

    }
}
