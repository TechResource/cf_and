package com.flightpathcore.objects;

import android.database.Cursor;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-14.
 */
public class ItemsDamagedObject {
    public Long id;
    public int eventId;
    public String imagePath;
    public String dmgDescription;
    public int isSent = 1;

    public ItemsDamagedObject(){

    }

    public ItemsDamagedObject(Cursor c){
        id = c.getLong(0);
        eventId = c.getInt(1);
        dmgDescription = c.getString(2);
        imagePath = c.getString(3);
        isSent = c.getInt(4);
    }
}
