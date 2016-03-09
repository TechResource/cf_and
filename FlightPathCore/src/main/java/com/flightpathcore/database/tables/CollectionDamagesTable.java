package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.ItemsDamagedObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-15.
 */
public class CollectionDamagesTable implements AbstractTable<CollectionDamagesObject> {

    public static final String TABLE_NAME = "collections_damage";
    public static final String COLLECTION_ID = "c_id";
    public static final String EVENT_ID = "c_event_id";
    public static final String X_POS = "c_xpos";
    public static final String Y_POS = "c_ypos";
    public static final String COLL_TYPE = "c_type";
    public static final String C_SPARE = "c_spare";
    public static final String C_DRIVER_BACK = "c_driver_back";
    public static final String C_PASSENGER_BACK = "c_passenger_back";
    public static final String C_DRIVER_FRONT = "c_driver_front";
    public static final String C_PASSENGER_FRONT = "c_passenger_front";
    public static final String C_DESCRIPTION = "c_description";
    public static final String C_DOUBLE_TYRES = "c_double_tyres";
    public static final String IS_SENT = "is_sent";

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            COLLECTION_ID + " integer primary key autoincrement," +
            EVENT_ID + " integer, " +
            X_POS + " integer, " +
            Y_POS + " integer, " +
            COLL_TYPE + " text, " +
            C_SPARE + " text, " +
            C_DRIVER_BACK + " text, " +
            C_PASSENGER_BACK + " text, " +
            C_DRIVER_FRONT + " text, " +
            C_PASSENGER_FRONT + " text, " +
            C_DESCRIPTION + " text, " +
            C_DOUBLE_TYRES + " integer, " +
            IS_SENT + " integer " +
            ");";

    private static final String[] allColumns = {COLLECTION_ID, EVENT_ID, X_POS, Y_POS, COLL_TYPE, C_SPARE, C_DRIVER_BACK, C_PASSENGER_BACK,
            C_DRIVER_FRONT, C_PASSENGER_FRONT, C_DESCRIPTION, C_DOUBLE_TYRES, IS_SENT};

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return COLLECTION_ID + " = " + id;
    }

//    public String getDamagesNotSent(String eventIdTo) {
//        return EVENT_ID + " <= " + eventIdTo + " AND " + IS_SENT + " = 0 ";
//    }

    @Override
    public String getOutputClassName() {
        return CollectionDamagesObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return COLLECTION_ID;
    }

    @Override
    public ContentValues getContentValues(CollectionDamagesObject object) {
        ContentValues values = new ContentValues();
//        values.put(COLLECTION_ID, object.id);
        values.put(EVENT_ID, object.eventId);
        values.put(X_POS, object.xPercent);
        values.put(Y_POS, object.yPercent);
        values.put(COLL_TYPE, object.getTypeAsString());
        values.put(IS_SENT, object.isSent);
        values.put(C_DESCRIPTION, object.description);
        values.put(C_DOUBLE_TYRES, object.dualTyres ? 1 : 0);
        values.put(C_SPARE, object.spare);
        values.put(C_DRIVER_BACK, object.driverBack);
        values.put(C_DRIVER_FRONT, object.driverFront);
        values.put(C_PASSENGER_BACK, object.passengerBack);
        values.put(C_PASSENGER_FRONT, object.passengerFront);
        return values;
    }
}
