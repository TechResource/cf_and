package flightpath.com.testmodule.test_map_module;

import android.support.test.runner.AndroidJUnit4;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.network.SynchronizationHelper;
import com.flightpathcore.objects.TripObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import flightpath.com.mapmodule.TripStatusHelper;
import flightpath.com.testmodule.base.BaseFragmentTest;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-25.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestTripStatusHelper extends BaseFragmentTest {

    private TripStatusHelper tripStatusHelper;

    @Before
    public void init(){
        DBHelper.initInstance(getActivity());
        tripStatusHelper = new TripStatusHelper();
    }

//    @Test
//    public void testStartTrip(){
//        TripObject mockTrip = Mockito.mock(TripObject.class);
//        tripStatusHelper.startNewTrip(mockTrip);
//        Assert.assertNotNull(tripStatusHelper.getCurrentTrip());
//        Assert.assertEquals(TripObject.TripStatus.TRIP_STARTED, tripStatusHelper.getTripStatus());
//    }

//    @Test
//    public void testTripStatuses(){
        // first clear current trip
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED);
//        Assert.assertNull(tripStatusHelper.getCurrentTrip());

        //make sure all statuses will work when there is no trip
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED);

        //check set multiple time same statusEnum
//        TripObject mockTrip = Mockito.mock(TripObject.class);
//        tripStatusHelper.startNewTrip(mockTrip);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED);
//
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED);
//        Assert.assertNull(tripStatusHelper.getCurrentTrip());
//    }

//    @Test
//    public void testTripStatusListener(){
//        TripStatusHelper.TripStatusListener mockListener = Mockito.mock(TripStatusHelper.TripStatusListener.class);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED); //make sure no trip set
//        tripStatusHelper.addListener(mockListener);
//        Mockito.verify(mockListener, Mockito.times(1)).onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, null);
//        TripObject mockTrip = Mockito.mock(TripObject.class);
//        tripStatusHelper.startNewTrip(mockTrip);
//        Mockito.verify(mockListener, Mockito.times(1)).onTripStatusChanged(TripObject.TripStatus.TRIP_STARTED, mockTrip);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_PAUSED);
//        Mockito.verify(mockListener, Mockito.times(1)).onTripStatusChanged(TripObject.TripStatus.TRIP_PAUSED, mockTrip);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STARTED);
//        Mockito.verify(mockListener, Mockito.times(2)).onTripStatusChanged(TripObject.TripStatus.TRIP_STARTED, mockTrip);
//        tripStatusHelper.setTripStatus(TripObject.TripStatus.TRIP_STOPPED);
//        Mockito.verify(mockListener, Mockito.times(2)).onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, null);
//
//        //after remove listener it should not called
//        tripStatusHelper.removeListener(mockListener);
//        tripStatusHelper.startNewTrip(mockTrip);
//        Mockito.verify(mockListener, Mockito.times(0));
//    }
}
