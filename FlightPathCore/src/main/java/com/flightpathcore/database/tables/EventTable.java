package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.EventObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class EventTable implements AbstractTable<EventObject> {

    public static final String TABLE_NAME = "events";
    public static final String EVENT_ID = "e_id";
    public static final String EVENT_TYPE = "e_type";
    public static final String EVENT_LAT = "e_lat";
    public static final String EVENT_LNG = "e_lng";
    public static final String EVENT_TRIP_ID = "e_trip_id";
    public static final String EVENT_ON_PAUSE = "e_on_pause";
    public static final String EVENT_TIMESTAMP = "e_timestamp";
    public static final String EVENT_IS_SENT = "e_is_sent";
    public static final String EVENT_DRIVER_ID = "e_member_id";
    public static final String EVENT_TRIP_START_DATE = "e_trip_start_date";
    public static final String EVENT_BT_ENABLED = "e_bt_enabled";
    public static final String EVENT_DONGLE_CONNECTED = "e_dongle_connected";
    public static final String EVENT_OTHER_DATA = "e_other_data";

    public static final String[] allColumns = {EVENT_ID, EVENT_TYPE, EVENT_LAT, EVENT_LNG, EVENT_TRIP_ID, EVENT_ON_PAUSE, EVENT_TIMESTAMP,
            EVENT_IS_SENT, EVENT_DRIVER_ID, EVENT_TRIP_START_DATE, EVENT_BT_ENABLED, EVENT_DONGLE_CONNECTED, EVENT_OTHER_DATA};

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " ( " +
            EVENT_ID + " integer primary key autoincrement, " +
            EVENT_TYPE + " text, " +
            EVENT_LAT + " real, " +
            EVENT_LNG + " real, " +
            EVENT_TRIP_ID + " integer, " +
            EVENT_ON_PAUSE + " text, " +
            EVENT_TIMESTAMP + " text," +
            EVENT_IS_SENT + " integer," +
            EVENT_DRIVER_ID + " integer, " +
            EVENT_TRIP_START_DATE + " text, " +
            EVENT_BT_ENABLED + " integer, " +
            EVENT_DONGLE_CONNECTED + " integer, " +
            EVENT_OTHER_DATA + " text" +
            ");";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return EVENT_ID + " = " + id;
    }

    @Override
    public String getOutputClassName() {
        return EventObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return EVENT_ID;
    }

    @Override
    public ContentValues getContentValues(EventObject event) {
        ContentValues values = new ContentValues();
        values.put(EVENT_TYPE, event.getTypeAsString());
        values.put(EVENT_TIMESTAMP, event.timestamp);
        values.put(EVENT_IS_SENT, event.isSent ? 1 : 0);
        values.put(EVENT_DRIVER_ID, event.driverId);
        values.put(EVENT_BT_ENABLED, event.btEnabled);
        values.put(EVENT_DONGLE_CONNECTED, event.dongleEnabled);
        values.put(EVENT_TRIP_ID, event.tripId);
        values.put(EVENT_ON_PAUSE, event.onPause ? 1 : 0);
        values.put(EVENT_OTHER_DATA, event.customEventObject);
        values.put(EVENT_LNG, event.longitude);
        values.put(EVENT_LAT, event.latitude);
        values.put(EVENT_TRIP_START_DATE, event.startDateTrip);
        return values;
    }
}
