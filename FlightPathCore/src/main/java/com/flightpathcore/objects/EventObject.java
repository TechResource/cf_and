package com.flightpathcore.objects;

import android.database.Cursor;

import com.flightpathcore.utilities.Utilities;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class EventObject{
    @SerializedName("id")
    public long eventId;
    public EventType type;
    public String timestamp;
    public transient boolean isSent = false;
    public int driverId;
    @SerializedName("bt")
    public Integer btEnabled;
    @SerializedName("dongle")
    public Integer dongleEnabled;
    public long tripId = -1;
    public boolean onPause;
    public String customEventObject = null;
    @SerializedName("lat")
    public double latitude;
    @SerializedName("lng")
    public double longitude;
    public TripObject trip;
    public String startDateTrip;

    public EventObject(){

    }

    public EventObject(double latitude, double longitude, EventType type, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.timestamp = timestamp;
    }

    public EventObject(Cursor cursor){
        this.eventId = cursor.getInt(0);
        this.type = cursor.getString(1) != null ? getTypeFromString(cursor.getString(1)) : null;
        this.latitude = cursor.getDouble(2);
        this.longitude = cursor.getDouble(3);
        this.tripId = cursor.getInt(4);
        this.onPause = cursor.getInt(5) == 1 ? true : false;
        if(this.type == EventType.INSPECTION ) {
            this.timestamp = Utilities.getUtcDateTime(cursor.getLong(6));
        }else if(this.type == EventType.LOGOUT){
            this.timestamp = cursor.getString(6);
        }else {
            this.timestamp = cursor.getLong(6) + "";
        }
        this.isSent = cursor.getInt(7) == 1 ? true : false;
        this.driverId = cursor.getInt(8);
        this.startDateTrip = cursor.getString(9);
        this.btEnabled = cursor.getInt(10);
        this.dongleEnabled = cursor.getInt(11);
        this.customEventObject = cursor.getString(12);

    }

    private EventType getTypeFromString(String type){
        if(type.equals("LOCATION")){
            return EventType.LOCATION;
        }else if(type.equals("PAUSE")){
            return EventType.PAUSE;
        }else if(type.equals("RESUME")){
            return EventType.RESUME;
        }else if(type.equals("START")){
            return EventType.START;
        }else if(type.equals("STOP")){
            return EventType.STOP;
        }else if(type.equals("POINT")){
            return EventType.POINT;
        }else if(type.equals("STATUS")){
            return EventType.STATUS;
        }else if(type.equals("INSPECTION")){
            return EventType.INSPECTION;
        }else if(type.equals("CHANGE_MILEAGE")){
            return EventType.CHANGE_MILEAGE;
        }else if(type.equals("DISPOSAL_INSPECTION")){
            return EventType.DISPOSAL_INSPECTION;
        }else if(type.equals("LOGOUT")){
            return EventType.LOGOUT;
        }

        throw new IllegalArgumentException("wrong type :"+type);
    }

    public String getTypeAsString(){
        switch (type){
            case LOCATION:
                return "LOCATION";
            case PAUSE:
                return "PAUSE";
            case RESUME:
                return "RESUME";
            case START:
                return "START";
            case STOP:
                return "STOP";
            case POINT:
                return "POINT";
            case STATUS:
                return "STATUS";
            case INSPECTION:
                return "INSPECTION";
            case CHANGE_MILEAGE:
                return "CHANGE_MILEAGE";
            case DISPOSAL_INSPECTION:
                return "DISPOSAL_INSPECTION";
            case LOGOUT:
                return "LOGOUT";
        }

        throw new IllegalArgumentException("wrong type");
    }

    public static final String getTypeAsString(EventType type){
        switch (type){
            case LOCATION:
                return "LOCATION";
            case PAUSE:
                return "PAUSE";
            case RESUME:
                return "RESUME";
            case START:
                return "START";
            case STOP:
                return "STOP";
            case POINT:
                return "POINT";
            case STATUS:
                return "STATUS";
            case INSPECTION:
                return "INSPECTION";
            case CHANGE_MILEAGE:
                return "CHANGE_MILEAGE";
            case DISPOSAL_INSPECTION:
                return "DISPOSAL_INSPECTION";
            case LOGOUT:
                return "LOGOUT";
        }

        throw new IllegalArgumentException("wrong type");
    }

    public enum EventType{
        LOCATION,
        PAUSE,
        RESUME,
        START,
        STOP,
        POINT,
        INSPECTION,
        DISPOSAL_INSPECTION,
        STATUS,
        CHANGE_MILEAGE,
        LOGOUT
    }
}
