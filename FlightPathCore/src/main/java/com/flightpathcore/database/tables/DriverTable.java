package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.UserObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class DriverTable implements AbstractTable<UserObject> {
    public static final int HELPER_ID = 1;

    public static final String TABLE_NAME = "driver";
    public static final String DRIVER_HELPER_ID = "d_hid";
    public static final String DRIVER_ID = "d_id";
    public static final String DRIVER_REGISTRATION_NUMBER = "d_registration_number";
    public static final String DRIVER_GPS_POINTER_PER = "d_gps_per";
    public static final String DRIVER_SYNC_PER = "d_sync_per";
    public static final String DRIVER_ACCESS = "d_access";
    public static final String DRIVER_TOKEN_ID = "d_token_id";
    public static final String DRIVER_NAME = "d_name";

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            DRIVER_HELPER_ID + " integer primary key," +
            DRIVER_ID + " integer," +
            DRIVER_REGISTRATION_NUMBER + " text," +
            DRIVER_GPS_POINTER_PER + " integer," +
            DRIVER_ACCESS + " text, " +
            DRIVER_NAME + " text, " +
            DRIVER_TOKEN_ID + " integer, " +
            DRIVER_SYNC_PER + " integer " + ");";

    private static final String[] driverAllColumns = { DRIVER_ID, DRIVER_REGISTRATION_NUMBER, DRIVER_GPS_POINTER_PER, DRIVER_SYNC_PER, DRIVER_ACCESS, DRIVER_TOKEN_ID, DRIVER_NAME};

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return DRIVER_HELPER_ID + " = '" + HELPER_ID + "'";
    }

    @Override
    public String getOutputClassName() {
        return UserObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return driverAllColumns;
    }

    @Override
    public String getIdColumn() {
        return DRIVER_ID;
    }

    @Override
    public ContentValues getContentValues(UserObject user) {
        ContentValues values = new ContentValues();
        values.put(DRIVER_ID, user.driverId);
        values.put(DRIVER_HELPER_ID, HELPER_ID);
        values.put(DRIVER_REGISTRATION_NUMBER, user.vehicleRegistration);
        values.put(DRIVER_GPS_POINTER_PER, user.gpsPointPer);
        values.put(DRIVER_SYNC_PER, user.synchronizationPer);
        values.put(DRIVER_ACCESS, user.access);
        values.put(DRIVER_TOKEN_ID, user.tokenId);
        values.put(DRIVER_NAME, user.name);

        return values;
    }
}
