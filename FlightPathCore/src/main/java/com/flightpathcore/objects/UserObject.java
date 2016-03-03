package com.flightpathcore.objects;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-21.
 */
public class UserObject {
    public String access;
    @SerializedName("token_id")
    public int tokenId;
    public String lifetime;
    public String timestamp;
    @SerializedName("vehicle_reg_number")
    public String vehicleRegistration;
    @SerializedName("driver_id")
    public int driverId;
    @SerializedName("gps_point_per")
    public int gpsPointPer;
    @SerializedName("synchronization_per")
    public int synchronizationPer;
    public UpdateAppObject update;
    public String name;

    public String email, password;

    public UserObject(){

    }

    public UserObject(Cursor cursor){
        this.driverId = cursor.getInt(0);
        this.vehicleRegistration = cursor.getString(1);
        this.gpsPointPer = cursor.getInt(2);
        this.synchronizationPer = cursor.getInt(3);
        this.access = cursor.getString(4);
        this.tokenId = cursor.getInt(5);
        this.name = cursor.getString(6);
    }
}
