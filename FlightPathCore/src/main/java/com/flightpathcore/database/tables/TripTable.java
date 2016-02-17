package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.TripObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-23.
 */
public class TripTable implements AbstractTable<TripObject> {

    public static final String TABLE_NAME = "trips";
    public static final String TRIP_ID = "t_id";
    public static final String TRIP_START_LAT = "t_start_lat";
    public static final String TRIP_START_LON = "t_start_lng";
    public static final String TRIP_END_LAT = "t_end_lat";
    public static final String TRIP_END_LON = "t_end_lnd";
    public static final String TRIP_START_MILEAGE = "t_start_mileage";
    public static final String TRIP_END_MILEAGE = "t_end_mileage";
    public static final String TRIP_STATUS = "t_status";
    public static final String TRIP_JOB_ID = "t_job_id";
    public static final String TRIP_REGISTRATION = "t_vrn";
    public static final String TRIP_IS_JOB_DONE = "t_job_done_id";
    public static final String TRIP_DONGLE_CONNECTION_TIME = "t_dongle_connection_time";
    public static final String TRIP_DRIVER_ID = "t_driver_id";
    public static final String TRIP_REASON = "t_reason";
    public static final String TRIP_START_DATE_AS_TIMESTAMP = "t_start_date";

    private static final String[] allColumns = {TRIP_ID, TRIP_START_LAT, TRIP_START_LON, TRIP_END_LAT, TRIP_END_LON, TRIP_START_MILEAGE, TRIP_END_MILEAGE,
            TRIP_STATUS, TRIP_JOB_ID, TRIP_REGISTRATION, TRIP_IS_JOB_DONE, TRIP_DONGLE_CONNECTION_TIME, TRIP_DRIVER_ID, TRIP_REASON, TRIP_START_DATE_AS_TIMESTAMP};

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            TRIP_ID + " integer primary key," +
            TRIP_START_LAT + " real," +
            TRIP_START_LON + " real," +
            TRIP_END_LAT + " real," +
            TRIP_END_LON + " real," +
            TRIP_START_MILEAGE + " text," +
            TRIP_END_MILEAGE + " text," +
            TRIP_STATUS + " text," +
            TRIP_JOB_ID + " integer," +
            TRIP_REGISTRATION + " text," +
            TRIP_IS_JOB_DONE + " integer," +
            TRIP_DONGLE_CONNECTION_TIME + " text," +
            TRIP_REASON + " text," +
            TRIP_START_DATE_AS_TIMESTAMP + " real, " +
            TRIP_DRIVER_ID + " integer " + ");";


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return TRIP_ID + " = '" + id + "'";
    }

    @Override
    public String getOutputClassName() {
        return TripObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return TRIP_ID;
    }

    @Override
    public ContentValues getContentValues(TripObject tripObject) {
        ContentValues values = new ContentValues();
        values.put(TRIP_START_MILEAGE, tripObject.startMileage);
        values.put(TRIP_START_LAT, tripObject.startLat);
        values.put(TRIP_START_LON, tripObject.startLon);
        values.put(TRIP_STATUS, tripObject.getStatusAsString());
        values.put(TRIP_REGISTRATION, tripObject.vehicleRegistrationNumber);
        values.put(TRIP_DRIVER_ID, tripObject.driverId);
        values.put(TRIP_REASON, tripObject.reason);
        values.put(TRIP_END_LAT, tripObject.endLat);
        values.put(TRIP_END_LON, tripObject.endLon);
        values.put(TRIP_END_MILEAGE, tripObject.endMileage);
        values.put(TRIP_JOB_ID, tripObject.jobId);
        values.put(TRIP_IS_JOB_DONE, tripObject.isJobDone ? 1 : 0);
        values.put(TRIP_START_DATE_AS_TIMESTAMP, tripObject.startDateAsTimestamp);

        return values;
    }
}
