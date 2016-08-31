package flightpath.com.mapmodule.di;

import dagger.Component;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.SpeedService;
import flightpath.com.mapmodule.TripStartStopWidget;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Component(modules = MapModule.class)
@MapScope
public interface MapComponent {
    TripStatusHelper tripStatusHelper();
    LocationHandler locationHandler();
    SpeedService speedService();

    void inject(MapFragment fragment);
    void inject(TripStartStopWidget widget);
}
