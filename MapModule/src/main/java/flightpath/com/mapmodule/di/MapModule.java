package flightpath.com.mapmodule.di;

import dagger.Module;
import dagger.Provides;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.SpeedService;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-24.
 */
@Module
public class MapModule {

    @MapScope
    @Provides
    public TripStatusHelper tripStatusHelper(){
        return new TripStatusHelper();
    }

    @MapScope
    @Provides
    public LocationHandler locationHandler(){
        return new LocationHandler();
    }

    @MapScope
    @Provides
    public SpeedService speedService(){
        return new SpeedService();
    }
}
