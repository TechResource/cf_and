package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.ItemsDamagedObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-15.
 */
public class ItemsDamagedTable implements AbstractTable<ItemsDamagedObject> {

    public static final String TABLE_NAME = "items_damaged";
    public static final String DAMAGE_ID = "_id";
    public static final String EVENT_ID = "event_id";
    private static final String DMG_DESCRIPTION = "i_dmg_description";
    private static final String PHOTO_FILE = "photo_path";
    public static final String IS_SENT = "is_sent";
    public static final String COLLECTION_ID = "collection_id";

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            DAMAGE_ID + " integer primary key autoincrement," +
            EVENT_ID + " integer, " +
            DMG_DESCRIPTION + " text, " +
            PHOTO_FILE + " text, " +
            IS_SENT + " integer, " +
            COLLECTION_ID + " integer " +
            ");";

    private static final String[] allColumns = {DAMAGE_ID, EVENT_ID, DMG_DESCRIPTION, PHOTO_FILE, IS_SENT, COLLECTION_ID};

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return DAMAGE_ID + " = " + id;
    }

    public String getDamagesNotSent(String eventIdTo) {
        return EVENT_ID + " <= " + eventIdTo + " AND " + IS_SENT + " = 0 ";
    }

    @Override
    public String getOutputClassName() {
        return ItemsDamagedObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return DAMAGE_ID;
    }

    @Override
    public ContentValues getContentValues(ItemsDamagedObject object) {
        ContentValues values = new ContentValues();
        values.put(DAMAGE_ID, object.id);
        values.put(EVENT_ID, object.eventId);
        values.put(DMG_DESCRIPTION, object.dmgDescription);
        values.put(PHOTO_FILE, object.imagePath);
        values.put(IS_SENT, object.isSent);
        values.put(COLLECTION_ID, object.collectionId);
        return values;
    }
}
