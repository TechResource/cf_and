package com.flightpathcore.objects;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-23.
 */
public class TripObject {
    @SerializedName("trip_id")
    public Long tripId;
    @SerializedName("start_mileage")
    public int startMileage;
    @SerializedName("start_lat")
    public double startLat;
    @SerializedName("start_lon")
    public double startLon;
    @SerializedName("end_lat")
    public double endLat;
    @SerializedName("end_lon")
    public double endLon;
    @SerializedName("end_mileage")
    public int endMileage;
    private transient TripStatus statusEnum;
    public String status;
    public String vehicleRegistrationNumber;
    public String reason;
    public boolean isJobDone;
    public int dongleConnectionTime;
    public long jobId;
    public int driverId;
    public long startDateAsTimestamp;
    @SerializedName("estimated_time")
    public int estimatedTime = 0;
    @SerializedName("start_location")
    public String startLocation = "";


    public TripObject(){

    }

    public TripObject(String reason, int startMileage, String vehicleRegistrationNumber, int driverId){
        this.reason = reason;
        this.startMileage = startMileage;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.driverId = driverId;
    }

    public TripObject(int startMileage, int jobId){
        this.startMileage = startMileage;
        this.jobId = jobId;
    }

    public TripObject(Cursor cursor){
        this.tripId = cursor.getLong(0);
        this.startLat = cursor.getDouble(1);
        this.startLon = cursor.getDouble(2);
        this.endLat = cursor.getDouble(3);
        this.endLon = cursor.getDouble(4);
        this.startMileage = cursor.getInt(5);
        this.endMileage = cursor.getInt(6);
        this.statusEnum = getStatusFromString(cursor.getString(7));
        this.jobId = cursor.getLong(8);
        this.vehicleRegistrationNumber = cursor.getString(9);
        this.isJobDone = cursor.getInt(10) == 1 ? true : false;
        this.dongleConnectionTime = cursor.getInt(11);
        this.driverId = cursor.getInt(12);
        this.reason = cursor.getString(13);
        this.startDateAsTimestamp = cursor.getLong(14);
    }

    public TripObject(Cursor cursor, boolean b) {
        this.tripId = cursor.getLong(13);
        this.startLat = cursor.getDouble(14);
        this.startLon = cursor.getDouble(15);
        this.endLat = cursor.getDouble(16);
        this.endLon = cursor.getDouble(17);
        this.startMileage = cursor.getInt(18);
        this.endMileage = cursor.getInt(19);
        this.setStatusEnum(getStatusFromString(cursor.getString(20)));
        this.jobId = cursor.getLong(21);
        this.vehicleRegistrationNumber = cursor.getString(22);
        this.isJobDone = cursor.getInt(23) == 1 ? true : false;
        this.dongleConnectionTime = cursor.getInt(24);
        this.driverId = cursor.getInt(25);
        this.reason = cursor.getString(26);
        this.startDateAsTimestamp = cursor.getLong(27);
    }

    public void setStatusEnum(TripStatus statusEnum){
        this.statusEnum = statusEnum;
        this.status = getStatusAsString();
    }

    public TripStatus getStatusEnum(){
        return statusEnum;
    }

    public String getStatusAsString() {
        if (statusEnum == null) {
            return null;
        }
        switch (statusEnum) {
            case TRIP_STARTED:
                return "STARTED";
            case TRIP_PAUSED:
                return "PAUSED";
            case TRIP_STOPPED:
                return "CLOSED";
            default:
                throw new IllegalArgumentException("wrong trip statusEnum: " + statusEnum);
        }
    }

    public TripStatus getStatusFromString(String s){
        if(s == null){
            return TripStatus.TRIP_STOPPED;
        }
        if(s.equalsIgnoreCase("STARTED")){
            return TripStatus.TRIP_STARTED;
        }else if(s.equalsIgnoreCase("PAUSED")){
            return TripStatus.TRIP_PAUSED;
        }else if(s.equalsIgnoreCase("STOPPED")){
            return TripStatus.TRIP_STOPPED;
        }else{
            return null;
//            throw new IllegalArgumentException("wrong trip statusEnum: "+s);
        }
    }

    public enum TripStatus {
        TRIP_STARTED, TRIP_PAUSED, TRIP_STOPPED
    }
}
