package com.flightpathcore.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flightpathcore.objects.SimpleListItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-07.
 */
@EViewGroup(resName = "widget_simple_list_item")
public class SimpleListItemWidget extends FrameLayout {

    @ViewById
    protected TextView leftText, rightText;
    private SimpleListItem simpleListItem;

    public SimpleListItemWidget(Context context) {
        super(context);
    }

    public SimpleListItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleListItemWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleListItemWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @AfterViews
    protected void init(){
        if(simpleListItem != null){
            fillView();
        }
    }

    public void setListItem(SimpleListItem item){
        this.simpleListItem = item;
        if(leftText != null){
            fillView();
        }
    }

    private void fillView() {
        leftText.setText(simpleListItem.leftText);
        rightText.setText(simpleListItem.rightText);
    }

}
