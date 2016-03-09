package com.flightpathcore.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.flightpathcore.database.tables.AbstractTable;
import com.flightpathcore.database.tables.CollectionDamagesTable;
import com.flightpathcore.database.tables.DriverTable;
import com.flightpathcore.database.tables.EventTable;
import com.flightpathcore.database.tables.ItemsDamagedTable;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.database.tables.PointsTable;
import com.flightpathcore.database.tables.TripTable;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.objects.CollectionDamagesObject;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.ItemsDamagedObject;
import com.flightpathcore.objects.TripObject;
import com.google.gson.Gson;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-22.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance = null;

    public static final String DATABASE_NAME = "fp.db";
    private static final int DATABASE_VERSION = 23;

    private String allColumnsEvent = null;

    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
    }

    public static synchronized DBHelper getInstance() {
        return instance;
    }

    //TODO move tables to modules? and provide them with @initInstance?
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        clearDB();
        EventTable et = new EventTable();
        TripTable tt = new TripTable();
        allColumnsEvent = "";
        for (String c : et.getAllColumns()) {
            allColumnsEvent += c + ",";
        }
        for (String c : tt.getAllColumns()) {
            allColumnsEvent += c + ",";
        }
        allColumnsEvent = allColumnsEvent.substring(0, allColumnsEvent.length() - 1);
    }

    public void clearDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DriverTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PointsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TripTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JobsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemsDamagedTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CollectionDamagesTable.TABLE_NAME);
        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DriverTable.CREATE_TABLE);
        db.execSQL(PointsTable.CREATE_TABLE);
        db.execSQL(EventTable.CREATE_TABLE);
        db.execSQL(TripTable.CREATE_TABLE);
        db.execSQL(JobsTable.CREATE_TABLE);
        db.execSQL(ItemsDamagedTable.CREATE_TABLE);
        db.execSQL(CollectionDamagesTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE " + TripTable.TABLE_NAME + " ADD COLUMN " + TripTable.TRIP_DONGLE_CONNECTION_TIME + " INTEGER DEFAULT -1");
        }
        if (oldVersion < 16) {
            db.execSQL("ALTER TABLE " + DriverTable.TABLE_NAME + " ADD COLUMN " + DriverTable.DRIVER_NAME + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + DriverTable.TABLE_NAME + " ADD COLUMN " + DriverTable.DRIVER_HELPER_ID + " INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + DriverTable.TABLE_NAME + " ADD COLUMN " + DriverTable.DRIVER_ACCESS + " INTEGER");
            db.execSQL("ALTER TABLE " + DriverTable.TABLE_NAME + " ADD COLUMN " + DriverTable.DRIVER_TOKEN_ID + " INTEGER");
        }
        if (oldVersion < 17) {
            try {
                db.execSQL("ALTER TABLE " + TripTable.TABLE_NAME + " ADD COLUMN " + TripTable.TRIP_DRIVER_ID + " INTEGER");
            } catch (Exception e) {
                //dont know why but sometime it happening and without try/catch it would not continue upgrading
            }
        }
        if (oldVersion < 18) {
            db.execSQL("ALTER TABLE " + DriverTable.TABLE_NAME + " ADD COLUMN " + DriverTable.DRIVER_NAME + " TEXT");
        }

        if(oldVersion < 21){
            db.execSQL("ALTER TABLE " + ItemsDamagedTable.TABLE_NAME + " ADD COLUMN " + ItemsDamagedTable.COLLECTION_ID+ " INTEGER");
        }

        if(oldVersion < 22){
            try {
                db.execSQL("ALTER TABLE " + CollectionDamagesTable.TABLE_NAME + " ADD COLUMN " + CollectionDamagesTable.C_DESCRIPTION + " TEXT DEFAULT ''");
            } catch (Exception e) {

            }
        }

        if(oldVersion < 23){
            try {
                db.execSQL("ALTER TABLE " + CollectionDamagesTable.TABLE_NAME + " ADD COLUMN " + CollectionDamagesTable.C_DOUBLE_TYRES + " INTEGER DEFAULT 0");
            } catch (Exception e) {

            }
        }
    }

    public void updateOrInsert(AbstractTable table, ContentValues values, String id) {
        int rowsAffected = getWritableDatabase().update(table.getTableName(), values, table.getWhereClause(id), null);
        if (rowsAffected <= 0) {
            getWritableDatabase().insertWithOnConflict(table.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long insert(AbstractTable table, ContentValues values) {
        if (table instanceof EventTable) {
            if (values.containsKey(EventTable.EVENT_IS_SENT) && values.getAsInteger(EventTable.EVENT_IS_SENT) == 0) {
                if (SynchronizationHelper.getInstance() != null) {
                    SynchronizationHelper.getInstance().increaseEventCounter();
                }
            }
        }

        long rowId = getWritableDatabase().insert(table.getTableName(), null, values);
        if (rowId == -1) {
            throw new IllegalArgumentException("something went wrong, insert values to db failed");
        }
        return rowId;
    }

    public void insertMultiple(AbstractTable table, List<ContentValues> values) {
        for (ContentValues value : values) {
            getWritableDatabase().insertWithOnConflict(table.getTableName(), null, value, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void remove(AbstractTable table, String id){
        getWritableDatabase().delete(table.getTableName(), table.getWhereClause(id), null);
    }

    public Object get(AbstractTable table, String id) {
        Cursor cursor;
        cursor = getReadableDatabase().query(table.getTableName(), table.getAllColumns(), table.getWhereClause(id), null, null, null, null);

        Class<?> returnClass;
        Constructor<?> constructor;
        Object toReturn;
        try {
            returnClass = Class.forName(table.getOutputClassName());
            constructor = returnClass.getConstructor(Cursor.class);
            if (cursor.moveToFirst()) {
                toReturn = constructor.newInstance(cursor);
            } else {
                Log.d("DB object GET", "return empty object");
                return null;
            }
            cursor.close();
            Log.d("DB object GET", new Gson().toJson(toReturn).toString());
            return toReturn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("something went wrong");
    }

    public Object getLast(AbstractTable table) {
        Cursor cursor;
        try {
            cursor = getReadableDatabase().query(table.getTableName(), table.getAllColumns(), null, null, null, null, table.getIdColumn() + " DESC", "1");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Class<?> returnClass;
        Constructor<?> constructor;
        Object toReturn;
        try {
            returnClass = Class.forName(table.getOutputClassName());
            constructor = returnClass.getConstructor(Cursor.class);
            if (cursor.moveToFirst()) {
                toReturn = constructor.newInstance(cursor);
            } else {
                Log.d("DB object GET LAST", "return empty object");
                return null;
            }
            cursor.close();
            Log.d("DB object GET LAST", new Gson().toJson(toReturn).toString());
            return toReturn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("something went wrong");
    }

    public List<? extends Object> getMultiple(AbstractTable table, String id) {
        return getMultiple(table, id, null, null);
    }

    public List<? extends Object> getMultiple(AbstractTable table, String id, String orderBy, Integer limit) {
        Cursor cursor = getReadableDatabase().query(table.getTableName(), table.getAllColumns(), table.getWhereClause(id), null, null, null, orderBy, limit != null ? limit + "" : null);
        Class<?> returnClass;
        Constructor<?> constructor;
        List<Object> toReturn = new ArrayList<>();
        try {
            returnClass = Class.forName(table.getOutputClassName());
            constructor = returnClass.getConstructor(Cursor.class);
            if (!cursor.moveToFirst()) {
                Log.d("DB object GET MULTIPLE", "return empty object");
                return new ArrayList<>();
            }
            while (!cursor.isAfterLast()) {
                toReturn.add(constructor.newInstance(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            Log.d("DB object GET MULTIPLE", new Gson().toJson(toReturn).toString());
            return toReturn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<EventObject> getEventsToSend(int limit) {
        List<EventObject> eventsToSend = new ArrayList<>();
        Cursor cursor;
        try {
//            cursor = getReadableDatabase().query(EventTable.TABLE_NAME, EventTable.allColumns, EventTable.EVENT_IS_SENT +" = 0 ", null, null, null, EventTable.EVENT_ID, limit+"");
            cursor = getReadableDatabase().rawQuery(
                    "SELECT " + allColumnsEvent + " FROM " + EventTable.TABLE_NAME + " events " +
                            //                        " LEFT JOIN " + PointsTable.TABLE_NAME + " points ON events." + EventTable.EVENT_POINT_ID + " = points." + PointsTable.POINT_ID +
                            " LEFT JOIN " + TripTable.TABLE_NAME + " trips ON events." + EventTable.EVENT_TRIP_ID + " = trips." + TripTable.TRIP_ID +
                            " WHERE events." + EventTable.EVENT_IS_SENT + " = 0 LIMIT 0," + limit,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
            return eventsToSend;
        }
        cursor.moveToFirst();
        EventObject e;
        while (!cursor.isAfterLast()) {
            e = new EventObject(cursor);
            if (e.type == EventObject.EventType.START || e.type == EventObject.EventType.STOP) {
                e.trip = new TripObject(cursor, true);
            }
            eventsToSend.add(e);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("DB object GET EVENTS", new Gson().toJson(eventsToSend).toString());
        return eventsToSend;
    }

    public int countEventsLeft() {
        try {
            Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM " + EventTable.TABLE_NAME + " WHERE " + EventTable.EVENT_IS_SENT + " = 0", null);
            cursor.moveToFirst();
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public void createEvent(EventObject event) {
        if (event.type == null) {
            throw new NullPointerException();
        }
        insert(new EventTable(), new EventTable().getContentValues(event));

    }

    public void setEventsSuccess(List<EventObject> eventsToSend) {
        EventTable et = new EventTable();
        ContentValues values = new ContentValues();
        values.put(EventTable.EVENT_IS_SENT, 1);
        for (EventObject e : eventsToSend) {
            updateOrInsert(new EventTable(), values, e.eventId + "");
        }
    }

    public void markDamagesReadyToSend(long eventId) {
        ContentValues values = new ContentValues();
        values.put(ItemsDamagedTable.IS_SENT, 0);
        getWritableDatabase().update(ItemsDamagedTable.TABLE_NAME, values, ItemsDamagedTable.EVENT_ID + "=" + eventId, null);
    }

    public List<ItemsDamagedObject> getDamagedItemsToSend(long eventIdTo) {
        ItemsDamagedTable idt = new ItemsDamagedTable();
        ArrayList<ItemsDamagedObject> items = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(idt.TABLE_NAME, idt.getAllColumns(), idt.EVENT_ID + " <= " + eventIdTo + " AND " + idt.IS_SENT + "=0 ", null, null, null, idt.DAMAGE_ID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(new ItemsDamagedObject(cursor));
            cursor.moveToNext();
        }
        return items;
    }

    public EventObject getLastSendEvent() {
        EventObject lastSendEvent = null;
        EventTable et = new EventTable();
        Cursor cursor = getReadableDatabase().query(et.getTableName(), et.getAllColumns(), et.EVENT_IS_SENT + " =1 ", null, null, null, et.EVENT_ID + " DESC", "1");
        if (cursor.moveToFirst()) {
            lastSendEvent = new EventObject(cursor);
        } else {
            lastSendEvent = (EventObject) getLast(et);
        }
        cursor.close();
        return lastSendEvent;
    }

    public void removeDamagedItemById(Long id) {
        ItemsDamagedTable idt = new ItemsDamagedTable();
        getWritableDatabase().delete(idt.getTableName(), idt.DAMAGE_ID + "=" + id, null);
    }

    public List<ItemsDamagedObject> getDamagesByEventId(String value) {
        ItemsDamagedTable idt = new ItemsDamagedTable();
        ArrayList<ItemsDamagedObject> items = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(idt.TABLE_NAME, idt.getAllColumns(), idt.EVENT_ID + " = " + value , null, null, null, idt.DAMAGE_ID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(new ItemsDamagedObject(cursor));
            cursor.moveToNext();
        }
        return items;
    }

    public List<CollectionDamagesObject> getCollectionsByEventId(String value) {
        CollectionDamagesTable ct = new CollectionDamagesTable();
        ArrayList<CollectionDamagesObject> items = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(ct.TABLE_NAME, ct.getAllColumns(), ct.EVENT_ID + " = " + value , null, null, null, ct.COLLECTION_ID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(new CollectionDamagesObject(cursor));
            cursor.moveToNext();
        }
        return items;
    }

    public List<ItemsDamagedObject> getDamagesByCollectionId(String value) {
        ItemsDamagedTable idt = new ItemsDamagedTable();
        ArrayList<ItemsDamagedObject> items = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(idt.TABLE_NAME, idt.getAllColumns(), idt.COLLECTION_ID + " = " + value , null, null, null, idt.DAMAGE_ID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(new ItemsDamagedObject(cursor));
            cursor.moveToNext();
        }
        return items;
    }
}
