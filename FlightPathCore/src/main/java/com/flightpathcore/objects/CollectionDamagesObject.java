package com.flightpathcore.objects;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-08.
 */
public class CollectionDamagesObject implements Serializable{
    public Long id;
    public int xPercent;
    public int yPercent;
    public Float x, y;
    public List<ItemsDamagedObject> damages;
    public CollectionType collectionType;
    public Long eventId;
    public boolean isSent;
    public String spare, driverBack, passengerBack, driverFront, passengerFront;
    public boolean dualTyres;
    public String description;

    public CollectionDamagesObject(){
        damages = new ArrayList<>();
    }

    public CollectionDamagesObject(float x, float y, Long eventId) {
        this.x = x;
        this.y = y;
        this.eventId = eventId;
        damages = new ArrayList<>();
    }

    public CollectionDamagesObject(Cursor c){
        this.id = c.getLong(0);
        this.eventId = c.getLong(1);
        this.xPercent = c.getInt(2);
        this.yPercent = c.getInt(3);
        String type = c.getString(4);
        if(type != null) {
            if (type.equalsIgnoreCase("INTERIOR")) {
                collectionType = CollectionType.INTERIOR;
            } else if (type.equalsIgnoreCase("EXTERIOR")) {
                collectionType = CollectionType.EXTERIOR;
            } else if (type.equalsIgnoreCase("TYRES")) {
                collectionType = CollectionType.TYRES;
            }
        }
        this.spare = c.getString(5);
        this.driverBack = c.getString(6);
        this.passengerBack = c.getString(7);
        this.driverFront = c.getString(8);
        this.passengerFront = c.getString(9);
        this.description = c.getString(10);
        this.dualTyres = c.getInt(11) == 1 ? true : false;
        isSent = c.getInt(12) == 1 ? true : false;
        damages = new ArrayList<>();
    }

    public String getTypeAsString() {
        switch (collectionType){
            case INTERIOR:
                return "INTERIOR";
            case EXTERIOR:
                return "EXTERIOR";
            case TYRES:
                return "TYRES";
        }
        throw new NullPointerException("wrong type");
    }

    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("collectionId", id);
            json.put("eventId", eventId);
            json.put("xPercent", xPercent);
            json.put("yPercent", yPercent);
            json.put("collectionType", getTypeAsString());
            json.put("spare", spare);
            json.put("driverBack", driverBack);
            json.put("passengerBack", passengerBack);
            json.put("driverFront", driverFront);
            json.put("passengerFront", passengerFront);
            json.put("dualTyres", dualTyres);
            json.put("description", description);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public enum CollectionType {
        INTERIOR,
        EXTERIOR,
        TYRES
    }
}
