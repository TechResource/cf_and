package com.flightpathcore.objects;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 09.05.2016.
 */
public class DisposalObject implements Serializable{
    public Long id;
    public long eventId;
    public List<String> imagePaths;
    public int isSent = 0;
    public String registrationNumber;

    public DisposalObject(){
        imagePaths = new ArrayList<>();
    }

    public DisposalObject(Cursor cursor){
        id = cursor.getLong(0);
        eventId = cursor.getLong(1);
        imagePaths = new Gson().fromJson(cursor.getString(2), new TypeToken<List<String>>(){}.getType());
    }

    public String getImage(int position) {
        return imagePaths.get(position);
    }
}
