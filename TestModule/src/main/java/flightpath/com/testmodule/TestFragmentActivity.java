package flightpath.com.testmodule;

import android.widget.FrameLayout;

import com.flightpathcore.base.BaseActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-11-19.
 */
@EActivity(resName = "test_fragment_activity")
public class TestFragmentActivity extends BaseActivity {

    @ViewById
    public FrameLayout testFragmentContainer;

}
