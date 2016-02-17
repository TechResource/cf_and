package com.connectedfleet.activity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.connectedfleet.activity.LoginActivity_;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-17.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity_>{

    public LoginActivityTest() {
        super(LoginActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testActivityExists(){
        Activity activity = getActivity();
        assertNotNull(activity);
    }
}
