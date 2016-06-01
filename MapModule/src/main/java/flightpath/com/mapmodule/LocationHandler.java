package flightpath.com.mapmodule;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.flightpathcore.base.BaseApplication;
import com.flightpathcore.base.LocationInterfacce;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.PointsTable;
import com.flightpathcore.objects.PointObject;
import com.flightpathcore.objects.UserObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class LocationHandler implements LocationInterfacce {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 second
    private LocationManager locationManager = null;
    private List<LocationAdditionalListener> additionalListeners = null;
    private boolean isReady = false;
    private LocationService gpsService, passiveService;

    @Inject
    public LocationHandler(){
        additionalListeners = new ArrayList<>();
    }

    public void initLocationHandler(Context context){
        UserObject user = (UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID + "");

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(BaseApplication.isDebug(context)) {
            if(passiveService == null)
                passiveService = new LocationService();
        }
        if(gpsService == null)
            gpsService = new LocationService();
        try {
            if(user != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, user.gpsPointPer * 1000, MIN_DISTANCE_CHANGE_FOR_UPDATES, gpsService);
                if(BaseApplication.isDebug(context))
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, user.gpsPointPer * 1000, MIN_DISTANCE_CHANGE_FOR_UPDATES, passiveService);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, gpsService);
                if(BaseApplication.isDebug(context))
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, passiveService);
            }
            isReady = true;
        }catch (SecurityException e){
            isReady = false;
            // permission not granted
        }
    }

    public boolean isReady(){
        return isReady;
    }

    public void onDestroy(){
        try {
            if(locationManager != null ) {
                if(gpsService != null)
                    locationManager.removeUpdates(gpsService);
                if(passiveService != null)
                    locationManager.removeUpdates(passiveService);
            }

        }catch (SecurityException e){
            // permission not granted
        }
        isReady = false;
    }

    @Override
    public Location getLocation(){
        if(gpsService != null && gpsService.getLocation() != null) {
            return gpsService.getLocation();
        }else if(passiveService != null && passiveService.getLocation() != null){
            return passiveService.getLocation();
        }
        return null;
    }

    private void notifyAllListeners(){
        Location location = getLocation();
        for(LocationAdditionalListener l : additionalListeners){
            l.onLocationChanged(location);
        }
    }

    public void addAdditionalListener(LocationAdditionalListener listener){
        if(!additionalListeners.contains(listener)) {
            additionalListeners.add(listener);
        }
    }

    public void removeAdditionalListener(LocationAdditionalListener listener){
        while(additionalListeners.remove(listener)){}
    }

    public interface LocationAdditionalListener{
        void onLocationChanged(Location location);
    }

    public class LocationService implements LocationListener{
        private Location latestLocation = null;

        public Location getLocation(){
            return latestLocation;
        }

        @Override
        public void onLocationChanged(Location location) {
            this.latestLocation = location;
            notifyAllListeners();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if(status == LocationProvider.OUT_OF_SERVICE ){
                latestLocation = null;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            latestLocation = null;
        }
    }
}
