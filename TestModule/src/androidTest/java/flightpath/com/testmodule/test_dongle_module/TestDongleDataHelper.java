package flightpath.com.testmodule.test_dongle_module;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import flightpath.com.donglemodule.DongleDataHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDongleDataHelper {

    private DongleDataHelper dongleDataHelper;

    @Before
    public void init(){
        dongleDataHelper = new DongleDataHelper();
    }

    @Test
    public void testDongleDataListener(){
        DongleDataHelper.DongleDataListener dongleDataListener = Mockito.mock(DongleDataHelper.DongleDataListener.class);
        dongleDataHelper.addListener(dongleDataListener);
        Mockito.verify(dongleDataListener, Mockito.times(1)).onDongleDataReceived(null);
        dongleDataHelper.removeListener(dongleDataListener);
        dongleDataHelper.onDataReceived(null);
        Mockito.verify(dongleDataListener, Mockito.times(1)).onDongleDataReceived(null);
    }

}
