package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.flightpathcore.objects.ListItem;

import org.androidannotations.annotations.EViewGroup;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-15.
 */
@EViewGroup(resName = "progress_list_item")
public class ProgressListItem extends RelativeLayout implements ListItem{
    public ProgressListItem(Context context) {
        super(context);
    }

    public ProgressListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
