package com.connectedfleet.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connectedfleet.R;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.utilities.AnimationHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tomaszszafran on 02/09/16.
 */
@EViewGroup(R.layout.widget_expandable_view)
public class ExpandableView extends LinearLayout {

    @ViewById
    protected CheckBox checkbox;
    @ViewById
    protected TextView description;
    @ViewById
    protected ImageView expandImg;
    @ViewById
    protected View root;
    @FragmentByTag
    protected HeaderFragment headerFragment;

    private boolean expanded = false;
    private float baseDescriptionHeight = 0;
    private String titleS, descriptionS;

    public ExpandableView(Context context) {
        super(context);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(attrs);
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(attrs);
    }

    private void readAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandableView,0,0);
        try {
            titleS = a.getString(R.styleable.ExpandableView_expand_title);
            descriptionS = a.getString(R.styleable.ExpandableView_expand_description);
        }finally {
            a.recycle();
        }
        if(description != null)
            fillView();
    }

    private void fillView(){
        checkbox.setText(titleS);
        description.setText(descriptionS);
        description.post(() -> {
            baseDescriptionHeight = description.getMeasuredHeight();
            description.setVisibility(GONE);
        });

    }

    @AfterViews
    protected void init(){
        root.setOnClickListener(view1 -> checkbox.toggle());

        headerFragment.setViewType(HeaderFragment.ViewType.CLEAR);
        if(titleS != null)
            fillView();

        expandImg.setOnClickListener(view -> {
            if(!expanded){
                AnimationHelper.expandAnimation(description, baseDescriptionHeight);
            }else{
                AnimationHelper.collapseAnimation(description, description.getMeasuredHeight());
            }
            expanded = !expanded;
        });
    }

    public String buildHtmlTag() {
        return (checkbox.isChecked() ? "&#10004; " : "&#10008; " )+titleS+"<br>";
    }
}
