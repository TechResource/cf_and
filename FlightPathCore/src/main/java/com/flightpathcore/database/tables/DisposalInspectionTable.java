package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.DisposalObject;
import com.google.gson.Gson;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 09.05.2016.
 */
public class DisposalInspectionTable implements AbstractTable<DisposalObject> {

    public static final String TABLE_NAME = "disposal_table";
    public static final String DISPOSAL_ID = "di_id";
    public static final String DISPOSAL_REGISTRATION_NUMBER = "di_registration_number";
    public static final String DISPOSAL_EVENT_ID = "di_e_id";
    public static final String DISPOSAL_IMAGE_PATH = "di_img_path";
    public static final String DISPOSAL_IS_SENT = "di_is_sent";

    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            DISPOSAL_ID + " integer primary key, " +
            DISPOSAL_REGISTRATION_NUMBER + " text, " +
            DISPOSAL_EVENT_ID + " integer, " +
            DISPOSAL_IMAGE_PATH + " text, " +
            DISPOSAL_IS_SENT + " integer " + ");";

    private static final String[] disposalAllColumns = {DISPOSAL_ID, DISPOSAL_EVENT_ID, DISPOSAL_IMAGE_PATH, DISPOSAL_IS_SENT, DISPOSAL_REGISTRATION_NUMBER};

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return DISPOSAL_ID +" = "+id;
    }

    @Override
    public String getOutputClassName() {
        return DisposalObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return disposalAllColumns;
    }

    @Override
    public String getIdColumn() {
        return DISPOSAL_ID;
    }

    @Override
    public ContentValues getContentValues(DisposalObject object) {
        ContentValues values = new ContentValues();
        values.put(DISPOSAL_IMAGE_PATH, new Gson().toJson(object.imagePaths));
        values.put(DISPOSAL_IS_SENT, object.isSent);
        values.put(DISPOSAL_EVENT_ID, object.eventId);
        values.put(DISPOSAL_REGISTRATION_NUMBER, object.registrationNumber);
        return values;
    }
}
