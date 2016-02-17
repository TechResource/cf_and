package flightpath.com.testmodule.base;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentTransaction;

import com.flightpathcore.base.BaseFragment;


import org.junit.Rule;

import flightpath.com.testmodule.TestFragmentActivity_;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-19.
 */
public class BaseFragmentTest{

    @Rule
    public ActivityTestRule<TestFragmentActivity_> activityRule = createActivityRule();

    protected ActivityTestRule createActivityRule(){
        return new ActivityTestRule<>(TestFragmentActivity_.class);
    }

    protected void startFragment(BaseFragment fragment) {
        FragmentTransaction transaction = activityRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(activityRule.getActivity().testFragmentContainer.getId(), fragment, "tag");
        transaction.commitAllowingStateLoss();
    }

    public Activity getActivity(){
        return activityRule.getActivity();
    }

    /**
     *
     * @param r Runnable to run on ui thread
     */
    public void ui(Runnable r){
        getActivity().runOnUiThread(r);
    }
}
