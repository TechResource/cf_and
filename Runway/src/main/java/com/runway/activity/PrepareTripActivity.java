package com.runway.activity;

import com.flightpathcore.fragments.HeaderFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-20.
 */

@EActivity(resName = "activity_prepare_trip")
public class PrepareTripActivity extends RunwayBaseActivity implements HeaderFragment.HeaderCallback {

    @FragmentByTag
    protected HeaderFragment headerFragment;

    @AfterViews
    protected void init(){
        headerFragment.setViewType(HeaderFragment.ViewType.PREPARE_TRIP);
        headerFragment.setHeaderCallback(this);
    }

    @Override
    public void onHeaderLeftBtnClick() {

    }

    @Override
    public void onHeaderRightBtnClick() {

    }

    @Override
    public void onMenuBtnClick() {

    }
}
