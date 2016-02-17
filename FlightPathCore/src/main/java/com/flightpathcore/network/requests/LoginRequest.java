package com.flightpathcore.network.requests;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public class LoginRequest {
    public String username;
    public String password;

    public LoginRequest(String email, String password){
        this.username = email;
        this.password = password;
    }
}
