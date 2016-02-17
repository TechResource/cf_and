package flightpath.com.testmodule.test_login_module;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import flightpath.com.loginmodule.LoginFragment_;
import flightpath.com.loginmodule.R;
import flightpath.com.testmodule.base.BaseFragmentTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-19.
 */

@RunWith(AndroidJUnit4.class)
public class TestLoginFragment extends BaseFragmentTest {

    @Before
    public void init() {
        startFragment(LoginFragment_.builder().build());
    }

    @Test
    public void checkLoginBtn() {
        //with two fields empty
        onView(withId(R.id.login)).check(matches(withText("")));
        onView(withId(R.id.password)).check(matches(withText("")));
        onView(withId(R.id.loginBtn)).check(matches(not(isEnabled())));

        //with one field empty
        onView(withId(R.id.login)).perform(typeText("test"));
        onView(withId(R.id.password)).check(matches(withText("")));
        onView(withId(R.id.loginBtn)).check(matches(not(isEnabled())));

        //with no field empty
        onView(withId(R.id.password)).perform(typeText("test"));
        onView(withId(R.id.loginBtn)).check(matches(isEnabled()));
    }


}
