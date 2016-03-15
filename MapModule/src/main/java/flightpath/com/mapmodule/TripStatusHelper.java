package flightpath.com.mapmodule;

import android.location.Location;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.TripTable;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class TripStatusHelper {

    private List<TripStatusListener> listeners = null;
    private TripObject currentTrip = null;
    private StopTripListener stopTripListener = null;

    @Inject
    public TripStatusHelper(){
        listeners = new ArrayList<>();
        currentTrip = (TripObject) DBHelper.getInstance().getLast(new TripTable());
    }

    public void setStopTripListener(StopTripListener listener){
        this.stopTripListener = listener;
    }

    public TripObject getCurrentTrip(){
        return currentTrip;
    }

    public TripObject.TripStatus getTripStatus(){
        return currentTrip != null ? currentTrip.getStatusEnum() : TripObject.TripStatus.TRIP_STOPPED;
    }

    public void startNewTrip(TripObject newTrip, Location currentLocation){
        currentTrip = newTrip;
        currentTrip.tripId = DBHelper.getInstance().insert(new TripTable(), new TripTable().getContentValues(currentTrip));
        setTripStatus(TripObject.TripStatus.TRIP_STARTED, currentLocation);
    }

    public void stopTrip(){
        if(stopTripListener != null){
            stopTripListener.onStopTrip();
        }
    }

    public void setTripStatus(TripObject.TripStatus tripStatus, Location currentLocation){
        setTripStatus(tripStatus, currentLocation, null);
    }

    public void setTripStatus(TripObject.TripStatus tripStatus, Location currentLocation, Integer endMileage){
        if(currentTrip == null){
            return;
        }

        if(currentTrip.getStatusEnum() == null){ // means it is a new trip
            currentTrip.setStatusEnum(tripStatus);
            createEvent(currentLocation, EventObject.EventType.START);
        }else{
            if(currentTrip.getStatusEnum() == TripObject.TripStatus.TRIP_PAUSED && tripStatus == TripObject.TripStatus.TRIP_STARTED){  // trip resumed
                currentTrip.setStatusEnum(tripStatus);
                createEvent(currentLocation, EventObject.EventType.RESUME);
            }else if(currentTrip.getStatusEnum() == TripObject.TripStatus.TRIP_STARTED && tripStatus == TripObject.TripStatus.TRIP_PAUSED) { // trip paused
                currentTrip.setStatusEnum(tripStatus);
                createEvent(currentLocation, EventObject.EventType.PAUSE);
            }else if(tripStatus == TripObject.TripStatus.TRIP_STOPPED){
                currentTrip.setStatusEnum(tripStatus);
                if(currentLocation != null) {
                    currentTrip.endLat = currentLocation.getLatitude();
                    currentTrip.endLon = currentLocation.getLongitude();
                }
                currentTrip.endMileage = endMileage != null ? endMileage : 0;
                createEvent(currentLocation, EventObject.EventType.STOP);
            }
        }
        DBHelper.getInstance().updateOrInsert(new TripTable(), new TripTable().getContentValues(currentTrip), currentTrip.tripId + "");

        if(tripStatus == TripObject.TripStatus.TRIP_STOPPED){
            currentTrip = null;
        }
        notifyAllListeners();
    }

    private void createEvent(Location currentLocation, EventObject.EventType eventType){
        EventObject eventObject = new EventObject();
        eventObject.timestamp = Utilities.getTimestamp()+"";
        eventObject.tripId = currentTrip.tripId;
        eventObject.driverId = ((UserObject) DBHelper.getInstance().get(new DriverTable(), DriverTable.HELPER_ID + "")).driverId;
        if(currentLocation != null){
            eventObject.longitude = currentLocation.getLongitude();
            eventObject.latitude = currentLocation.getLatitude();
        }
        eventObject.type = eventType;
        eventObject.startDateTrip = currentTrip.startDateAsTimestamp+"";
        DBHelper.getInstance().createEvent(eventObject);
    }

    private void notifyAllListeners(){
        for (TripStatusListener l : listeners) {
            if(currentTrip != null) {
                l.onTripStatusChanged(currentTrip.getStatusEnum(), currentTrip);
            }else{
                l.onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, currentTrip);
            }
        }
    }

    public void addListener(TripStatusListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);

        if(currentTrip != null) {
            listener.onTripStatusChanged(currentTrip.getStatusEnum() == null ? TripObject.TripStatus.TRIP_STOPPED : currentTrip.getStatusEnum(), currentTrip);
        }else{
            listener.onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, currentTrip);
        }
    }

    public void removeListener(TripStatusListener listener){
        while(listeners.remove(listener)){}
    }

    public interface TripStatusListener{
        void onTripStatusChanged(TripObject.TripStatus tripStatus, TripObject trip);
    }

    public interface StopTripListener{
        void onStopTrip();
    }

}
