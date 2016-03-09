package com.flightpathcore.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.flightpathcore.utilities.Utilities;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.overrideFonts(getContext(), view);
    }

    public abstract String getTitle();
    public abstract boolean onBackPressed();
}
