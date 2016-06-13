package flightpath.com.testmodule.base;

import android.test.InstrumentationTestCase;

import java.util.concurrent.CountDownLatch;
/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-17.
 */
public abstract class BaseUnitTest extends InstrumentationTestCase {
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public CountDownLatch getCountDownLatch(){
        return countDownLatch;
    }

    public void waitUntilCompleted(){
        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
