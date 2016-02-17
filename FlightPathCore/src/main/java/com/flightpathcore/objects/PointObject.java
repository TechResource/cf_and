package com.flightpathcore.objects;

import android.database.Cursor;

import com.flightpathcore.utilities.Utilities;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class PointObject{
    public Long pointId;
    public Long tripId;
    public double latitude;
    public double longitude;

    public PointObject(){

    }

    public PointObject(double latitude, double longitude, long tripId, boolean b) {
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointObject(Cursor cursor){
        this.pointId = cursor.getLong(0);
        this.tripId = cursor.getLong(1);
        this.latitude = cursor.getDouble(2);
        this.longitude = cursor.getDouble(3);
    }

}
