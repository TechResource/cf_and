package flightpath.com.testmodule.test_flight_path_core.api;

import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.AppObject;
import com.flightpathcore.base.NavigatorAbstract;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.requests.LoginRequest;
import com.flightpathcore.network.requests.SynchronizeRequest;
import com.flightpathcore.objects.EventObject;
import com.flightpathcore.objects.PointObject;
import com.flightpathcore.objects.UserObject;
import com.flightpathcore.utilities.Utilities;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.testmodule.base.BaseUnitTest;


/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-17.
 */
public class ApiTest extends BaseUnitTest {

    private String accessToken;
    private Integer tokenId;
    private Integer driverId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        FPModel.initInstance(getInstrumentation().getTargetContext());
//        AppCore.initInstance(new AppObject("test", "2.0", "2.0", "1"));
    }

    public void testLoginApi(){
//        Assert.assertNotNull(FPModel.getInstance());
//        FPModel.getInstance().fpApi.login(new LoginRequest("radek@appsvisio.com", "radek123"), new MyCallback<UserObject>() {
//            @Override
//            public void onSuccess(UserObject response) {
//                Assert.assertNotNull(response);
//                Assert.assertNotNull(response.access);
//                accessToken = response.access;
//                Assert.assertNotNull(response.tokenId);
//                tokenId = response.tokenId;
//                driverId = response.driverId;
//                getCountDownLatch().countDown();
//            }
//
//            @Override
//            public void onError(String error) {
//                throw new IllegalArgumentException(error);
//            }
//        });
//        waitUntilCompleted();
    }

    public void testSynchronization(){
//        Assert.assertNotNull(FPModel.getInstance());
//        testLoginApi(); // get credential
//        Assert.assertNotNull(tokenId);
//        Assert.assertNotNull(accessToken);
//        Assert.assertNotNull(driverId);
//        Integer response = FPModel.getInstance().fpApi.sendEvents(new SynchronizeRequest(tokenId,accessToken,createExampleEvents()));
//        Assert.assertNotNull(response);
    }

    private List<EventObject> createExampleEvents(){
        List<EventObject> eventsToSend = new ArrayList<>();
        PointObject location = new PointObject();
//        location.type = EventObject.EventType.LOCATION;
//        location.driverId = driverId;
//        location.timestamp = Utilities.getTimestamp();
        location.longitude = 18.609415;
        location.latitude = 54.3772;

        return eventsToSend;
    }
}
