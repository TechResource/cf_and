package flightpath.com.loginmodule;

import android.content.pm.PackageManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.SwipeableViewPager;
import com.flightpathcore.utilities.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-16.
 */
@EFragment(resName = "fragment_splash_screen")
public class SplashScreenFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @ViewById
    protected SwipeableViewPager pager;
    @ViewById
    protected TextView versionNumberView, appName;
    @ViewById
    protected Button loginButton;
    @ViewById
    protected ViewGroup dots;
    @ViewById
    protected ImageView dot1, dot2, dot3, dot4;

    private SplashScreenCallback callback = null;

    @AfterViews
    protected void init(){
        Utilities.setOswaldTypeface(getActivity().getAssets(), versionNumberView, appName, loginButton);
        pager.setSwipeAble(true);
        try {
            versionNumberView.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showPager(SplashScreenCallback callback){
        this.callback = callback;
//        dots.setVisibility(View.VISIBLE);
        pager.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);
    }

    @Click
    protected void loginButton(){
        if(callback != null){
            callback.onLoginBtnClick();
        }
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            InfoWidget iw = InfoWidget_.build(getContext());
            iw.setData(getString(R.string.info_window_text1), getResources().getDrawable(R.drawable.cf_bg2));
            container.addView(iw);
            return iw;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        dot1.setImageDrawable(getResources().getDrawable(R.drawable.dot));
        dot2.setImageDrawable(getResources().getDrawable(R.drawable.dot));
        dot3.setImageDrawable(getResources().getDrawable(R.drawable.dot));
        dot4.setImageDrawable(getResources().getDrawable(R.drawable.dot));
        if(position == 0){
            dot1.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
        }else if(position == 1){
            dot2.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
        }else if(position == 2){
            dot3.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
        }else if(position == 3){
            dot4.setImageDrawable(getResources().getDrawable(R.drawable.dot_selected));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface SplashScreenCallback{
        void onLoginBtnClick();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
