package com.flightpathcore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.base.AppCore;
import com.flightpathcore.base.BaseFragment;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.utilities.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
@EFragment(resName = "fragment_header")
public class HeaderFragment extends BaseFragment {

    @ViewById
    protected TextView appName, versionName;
    @ViewById
    protected ImageView menu;
    @ViewById
    protected Button leftBtn, rightBtn;

    private HeaderCallback headerCallback = null;
    private ViewType viewType = null;

    @AfterViews
    protected void init(){
        Utilities.setOswaldTypeface(getActivity().getAssets(), appName, versionName, leftBtn, rightBtn);
        appName.setText(AppCore.getInstance().getAppInfo().appName);
        versionName.setText(AppCore.getInstance().getAppInfo().appVersion +"-"+AppCore.getInstance().getAppInfo().appVersionCode);
        if(viewType != null){
            setupView();
        }
    }

    public void setViewType(ViewType viewType){
        this.viewType = viewType;
        if(menu != null) {
            setupView();
        }
    }

    private void setupView(){
        switch (viewType) {
            case LOGIN_ACTIVITY:
                menu.setVisibility(View.GONE);
                leftBtn.setVisibility(View.VISIBLE);
                leftBtn.setText("");
                break;
            case PREPARE_TRIP:
                menu.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
                rightBtn.setText(R.string.done_label);
                break;
            case MAIN_ACTIVITY:
                menu.setVisibility(View.VISIBLE);
                break;
            case INSPECTION:
                menu.setVisibility(View.GONE);
                break;
            case CLOSE_PERIOD:
                menu.setVisibility(View.GONE);
                break;
            case CLEAR:
                leftBtn.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
                menu.setVisibility(View.GONE);
                break;
            case DISPOSAL_INSPECTION:
                leftBtn.setVisibility(View.VISIBLE);
                leftBtn.setText(R.string.cancel_label);
                rightBtn.setVisibility(View.VISIBLE);
                rightBtn.setText(R.string.done_label);
                menu.setVisibility(View.GONE);
                break;
        }
    }

    @Click
    protected void menu(){
        if(headerCallback != null){
            headerCallback.onMenuBtnClick();
        }
    }

    @Click
    protected void leftBtn(){
        if(headerCallback != null){
            headerCallback.onHeaderLeftBtnClick();
        }
    }

    @Click
    protected void rightBtn(){
        if(headerCallback != null){
            headerCallback.onHeaderRightBtnClick();
        }
    }

    public void setHeaderCallback(HeaderCallback callback){
        this.headerCallback = callback;
    }

    public void setRightBtnEnabled(boolean enabled){
        rightBtn.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setRightBtnText(String text){
        if(text == null){
            rightBtn.setVisibility(View.GONE);
        }else {
            rightBtn.setText(text);
            rightBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setLeftBtnText(String text){
        if(text == null){
            leftBtn.setVisibility(View.GONE);
        }else {
            leftBtn.setText(text);
            leftBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getTitle() {
        return "header";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface HeaderCallback{
        void onHeaderLeftBtnClick();
        void onHeaderRightBtnClick();
        void onMenuBtnClick();
    }

    public enum ViewType{
        LOGIN_ACTIVITY,
        PREPARE_TRIP,
        MAIN_ACTIVITY,
        CLOSE_PERIOD,
        INSPECTION,
        DISPOSAL_INSPECTION,
        CLEAR
    }

}
