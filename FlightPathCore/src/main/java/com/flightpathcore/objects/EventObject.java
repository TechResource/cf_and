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
    public transient long timestamp;
    public transient boolean isSent = false;
    @SerializedName("timestamp")
    public String dateTime;
    public int driverId;
    @SerializedName("bt")
    public int btEnabled;
    @SerializedName("dongle")
    public int dongleEnabled;
    public long tripId;
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

    public EventObject(Cursor cursor){
        this.eventId = cursor.getInt(0);
        this.type = getTypeFromString(cursor.getString(1));
        this.latitude = cursor.getDouble(2);
        this.longitude = cursor.getDouble(3);
        this.tripId = cursor.getInt(4);
        this.onPause = cursor.getInt(5) == 1 ? true : false;
        this.timestamp = cursor.getLong(6);
        this.isSent = cursor.getInt(7) == 1 ? true : false;
        this.driverId = cursor.getInt(8);
        this.startDateTrip = cursor.getString(9);
        this.btEnabled = cursor.getInt(10);
        this.dongleEnabled = cursor.getInt(11);
        this.customEventObject = cursor.getString(12);
        this.dateTime = Utilities.getUtcDateTime(this.timestamp);
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
        STATUS;
    }
}
