package flightpath.com.testmodule.test_map_module;

import android.location.Location;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.flightpathcore.database.DBHelper;
import com.flightpathcore.objects.TripObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.MapCallbacks;
import flightpath.com.mapmodule.MapFragment;
import flightpath.com.mapmodule.MapFragment_;
import flightpath.com.mapmodule.R;
import flightpath.com.mapmodule.TripStatusHelper;
import flightpath.com.mapmodule.di.DIMapModule;
import flightpath.com.mapmodule.di.DaggerTestMapComponent;
import flightpath.com.mapmodule.di.TestMapModule;
import flightpath.com.testmodule.TestFragmentActivity_;
import flightpath.com.testmodule.base.BaseFragmentTest;
import flightpath.com.testmodule.base.DaggerActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.not;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-20.
 */
@RunWith(AndroidJUnit4.class)
public class TestMapFragment extends BaseFragmentTest {

    private MapFragment mapFragment;
    private TripStatusHelper tripStatusHelper;
    private LocationHandler locationHandler;

    @Override
    protected ActivityTestRule createActivityRule() {
        tripStatusHelper = Mockito.mock(TripStatusHelper.class);
        locationHandler = Mockito.mock(LocationHandler.class);

        return new DaggerActivityTestRule<>(TestFragmentActivity_.class, (application, activity) -> {
            DIMapModule.diMapModule().setTestComponent(DaggerTestMapComponent.builder().testMapModule(new TestMapModule(tripStatusHelper, locationHandler)).build());
        });
    }

    @Before
    public void init() {
        DBHelper.initInstance(getActivity());
        mapFragment = MapFragment_.builder().build();
        startFragment(mapFragment);
    }

    @Test
    public void testFragmentPresents() {
        onView(withId(R.id.trackLocationBtn));
    }

    @Test
    public void testTripStatuses(){
        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, null));
        onView(withId(R.id.tripStartStopWidget)).check(matches(not(isDisplayed())));

        TripObject mockTrip = mock(TripObject.class);
        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_STARTED, mockTrip));
        onView(withId(R.id.tripStartStopWidget)).check(ViewAssertions.matches(isDisplayed()));

        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_PAUSED, mockTrip));
        onView(withId(R.id.tripStartStopWidget)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testLocationChanging(){
        Location fakeLocation = new Location("Mock provider");
        fakeLocation.setLatitude(51.507);
        fakeLocation.setLongitude(-0.128);
        when(locationHandler.getLocation()).thenReturn(fakeLocation);

        ui(() -> mapFragment.onLocationChanged(fakeLocation));
//        onView(withId(R.id.longitudeValue)).check(matches(withText(Utilities.formatNumber(fakeLocation.getLongitude()))));
//        onView(withId(R.id.latitudeValue)).check(matches(withText(Utilities.formatNumber(fakeLocation.getLatitude()))));
    }

    @Test
    public void testPreparingTrip(){
        MapCallbacks mockCallbacks = mock(MapCallbacks.class);
        Location fakeLocation = new Location("Mock provider");
        fakeLocation.setLatitude(51.507);
        fakeLocation.setLongitude(-0.128);

        ui(() -> mapFragment.setCallbacks(mockCallbacks));
        //without trip and location - should display toast / do nothing
        when(locationHandler.getLocation()).then(invocationOnMock -> null);
        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, null));
        onView(withId(R.id.tripStatusLabel)).perform(ViewActions.click());
        verify(mockCallbacks, times(0)).onPrepareTrip();

        //with trip and without location - nothing should happen
        TripObject mockTrip = mock(TripObject.class);
        mockTrip.setStatusEnum(TripObject.TripStatus.TRIP_STARTED);
        when(locationHandler.getLocation()).thenReturn(fakeLocation);
        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_STARTED, mockTrip));
        onView(withId(R.id.tripStatusLabel)).perform(ViewActions.click());
        verify(mockCallbacks, times(0)).onPrepareTrip();

        //without trip with location - should call MapCallback.onPrepareTrip()
        when(locationHandler.getLocation()).thenReturn(fakeLocation);
        ui(() -> mapFragment.onTripStatusChanged(TripObject.TripStatus.TRIP_STOPPED, null));
        onView(withId(R.id.tripStatusLabel)).perform(ViewActions.click());
        verify(mockCallbacks, times(1)).onPrepareTrip();



    }
}
