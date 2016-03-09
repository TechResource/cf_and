package com.flightpathcore.objects;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-14.
 */
public class ItemsDamagedObject implements Serializable {
    public Long id;
    public long eventId;
    public String imagePath;
    public String dmgDescription;
    public int isSent = 1;

    //gemini
    public Long collectionId = null;

    public ItemsDamagedObject(){

    }

    public ItemsDamagedObject(Cursor c){
        id = c.getLong(0);
        eventId = c.getLong(1);
        dmgDescription = c.getString(2);
        imagePath = c.getString(3);
        isSent = c.getInt(4);
        collectionId = c.getLong(5);
    }

    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("eventId", eventId);
            json.put("imagePath", imagePath);
            json.put("dmgDescription", dmgDescription);
            json.put("isSent", isSent);
            json.put("collectionId", collectionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
