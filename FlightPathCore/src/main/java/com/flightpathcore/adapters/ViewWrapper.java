package com.flightpathcore.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-08.
 */
public class ViewWrapper<V extends View> extends RecyclerView.ViewHolder {

    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }
}
