package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.PointObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class PointsTable implements AbstractTable<PointObject>{

    public static final String TABLE_NAME = "POINTS_TABLE";
    public static final String POINT_ID = "p_id";
    private static final String POINT_TRIP_ID = "p_trip_id";
    private static final String POINT_LAT = "p_lat";
    private static final String POINT_LON = "p_lon";

    private static final String[] allColumns = {POINT_ID, POINT_TRIP_ID, POINT_LAT, POINT_LON};

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            POINT_ID + " integer primary key," +
            POINT_LAT + " real, " +
            POINT_LON + " real, " +
            POINT_TRIP_ID + " integer " + ");";



    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return POINT_TRIP_ID +" = "+id;
    }

    @Override
    public String getOutputClassName() {
        return PointObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return POINT_ID;
    }

    @Override
    public ContentValues getContentValues(PointObject point) {
        ContentValues values = new ContentValues();
        values.put(POINT_TRIP_ID, point.tripId);
        values.put(POINT_LAT, point.latitude);
        values.put(POINT_LON, point.longitude);

        return values;
    }
}
