package com.flightpathcore.network.requests;

import com.flightpathcore.objects.EventObject;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-05-25.
 */
public class SynchronizeRequest {
    public int token_id;
    public String access;
    public List<EventObject> events;

    public SynchronizeRequest(int tokenId, String accessToken, List<EventObject> events){
        this.token_id = tokenId;
        this.access = accessToken;
        this.events = events;
    }

}
