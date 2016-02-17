package com.flightpathcore.network.requests;

import com.flightpathcore.objects.UserObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-14.
 */
public class JobsRequest {
    public int token_id;
    public String access;

    public JobsRequest(int tokenId, String access){
        this.token_id = tokenId;
        this.access = access;
    }

    public JobsRequest(UserObject userSession) {
        token_id = userSession.tokenId;
        access = userSession.access;
    }
}
