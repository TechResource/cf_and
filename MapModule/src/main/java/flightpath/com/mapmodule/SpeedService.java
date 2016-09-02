package flightpath.com.mapmodule;

import android.location.Location;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by tomaszszafran on 31/08/16.
 */
public class SpeedService implements LocationHandler.LocationAdditionalListener{

    private double currentAvgSpeed = 0;
    private int speedCounter = 0;
    private Location previousLocation = null;
    private static double earthRadius = 6378100;

    @Inject
    public SpeedService(){
        currentAvgSpeed = 0;
    }

    public double getCurrentAvgSpeed(){
        return currentAvgSpeed;
    }

    public double getCurrentAvgSpeedAndClear(){
        double avgSpeed = currentAvgSpeed;
        currentAvgSpeed = 0;
        speedCounter = 0;
        return avgSpeed;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            if (previousLocation != null) {
                double currentMph = calculateAvgSpeed(previousLocation, location);
                if(currentMph > 0) {
                    speedCounter += 1;
                    currentAvgSpeed = (currentAvgSpeed + currentMph) / speedCounter;
                }
                previousLocation = location;
            } else {
                previousLocation = location;
                speedCounter = 1;
            }
        }
    }

    private double calculateAvgSpeed(Location prevLocation, Location newLocation){
        double lat1,lat2,lon1,lon2;
        lat1 = prevLocation.getLatitude() * Math.PI / 180;
        lon1 = prevLocation.getLongitude() * Math.PI / 180;
        lat2 = newLocation.getLatitude() * Math.PI / 180;
        lon2 = newLocation.getLongitude() * Math.PI / 180;

        // P
        double rho1 = earthRadius * Math.cos(lat1);
        double z1 = earthRadius * Math.sin(lat1);
        double x1 = rho1 * Math.cos(lon1);
        double y1 = rho1 * Math.sin(lon1);

        // Q
        double rho2 = earthRadius * Math.cos(lat2);
        double z2 = earthRadius * Math.sin(lat2);
        double x2 = rho2 * Math.cos(lon2);
        double y2 = rho2 * Math.sin(lon2);

        // Dot product
        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (earthRadius * earthRadius);

        double theta = Math.acos(cos_theta);

        // Distance in Metres
        double distance = earthRadius * theta;

        double time_s = (newLocation.getTime() - prevLocation.getTime()) / 1000.0;
        if(time_s > 0 && distance > 0) {
            double speed_mps = distance / time_s;
            double speed_mph = (speed_mps * 3600.0) / 1609.344;
            double speed_kph = (speed_mps * 3600.0) / 1000;
            return speed_kph;
        }else{
            return 0;
        }


    }
}
