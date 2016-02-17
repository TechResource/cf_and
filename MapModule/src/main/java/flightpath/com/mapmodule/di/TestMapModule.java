package flightpath.com.mapmodule.di;

import dagger.Module;
import dagger.Provides;
import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
@MapScope
@Module
public class TestMapModule {

    private TripStatusHelper tripStatusHelper;
    private LocationHandler locationHandler;

    public TestMapModule(TripStatusHelper tripStatusHelper, LocationHandler locationHandler){
        this.tripStatusHelper = tripStatusHelper;
        this.locationHandler = locationHandler;
    }

    @MapScope
    @Provides
    public TripStatusHelper tripStatusHelper(){
        return tripStatusHelper;
    }

    @MapScope
    @Provides
    public LocationHandler locationHandler(){
        return locationHandler;
    }
}
