package com.connectedfleet;

import android.test.ApplicationTestCase;

import com.flightpathcore.base.AppCore;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.network.SynchronizationHelper;

public class ApplicationTest extends ApplicationTestCase<ConnectedFleetApplication> {

    private ConnectedFleetApplication app;

    public ApplicationTest() {
        super(ConnectedFleetApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        app = getApplication();
    }

    public void testSingletons() {
        assertNotNull(AppCore.getInstance());
//        assertNotNull(FPModel.getInstance());
        assertNotNull(DBHelper.getInstance());
//        assertNotNull(TripStatusHelper.getInstance());
        assertNotNull(SynchronizationHelper.getInstance());
    }
}