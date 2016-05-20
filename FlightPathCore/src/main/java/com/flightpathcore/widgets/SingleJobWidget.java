package com.flightpathcore.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flightpathcore.R;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.utilities.AnimationHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 20.05.2016.
 */
@EViewGroup( resName = "widget_single_job")
public class SingleJobWidget extends LinearLayout {

    @ViewById
    protected TextView jobName, jobDate;
    @ViewById
    protected ImageView rightIconDropdown;
    @ViewById
    protected JobInfoWidget jobInfo;
    @ViewById
    protected RelativeLayout clickable;
    @ViewById
    protected LinearLayout jobInfoContainer;
    @ViewById
    protected Button selectJobBtn;

    private JobObject jobObject;
    private boolean collapsed = false;
    private BaseActivity.JobSelectCallback jobSelectCallback;

    public SingleJobWidget(Context context) {
        super(context);
    }

    public SingleJobWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleJobWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void init(){
        if(jobObject != null)
            fillView();
    }

    @Click
    protected void clickable(){
        if( (jobSelectCallback.getSelectedJobId() == null || jobSelectCallback.getSelectedJobId() == -1 ) && jobObject.id == -1 && collapsed)
            return;
        if(collapsed){
            expand();
        }else{
            collapse();
        }
    }

    private void expand(){
        AnimationHelper.expandAnimation(jobInfoContainer, jobObject.baseHeight);
        rightIconDropdown.setRotation(180f);
        collapsed = false;
        jobObject.expanded = true;
    }

    private void collapse(){
        AnimationHelper.collapseAnimation(jobInfoContainer, jobObject.baseHeight);
        rightIconDropdown.setRotation(0f);
        collapsed = true;
        jobObject.expanded = false;
    }

    public void setData(JobObject data, BaseActivity.JobSelectCallback jobSelectCallback){
        jobObject = data;
        this.jobSelectCallback = jobSelectCallback;
        if(jobName != null)
            fillView();
    }

    private void fillView() {
        jobName.setText(jobObject.name);
        jobDate.setText(jobObject.dateFrom);

        if( (jobSelectCallback.getSelectedJobId() != null && jobObject.id == jobSelectCallback.getSelectedJobId()) ||
                (jobSelectCallback.getSelectedJobId() == null && jobObject.id == -1 ) ){
//            if(jobObject.id == -1){
//                selectJobBtn.setVisibility(GONE);
//            }
            selectJobBtn.setText(R.string.unselect_job_label);
            clickable.setBackgroundResource(R.color.blue_color);
            jobName.setTextColor(getResources().getColor(R.color.white));
            jobDate.setTextColor(getResources().getColor(R.color.white));
        }else{
//            selectJobBtn.setVisibility(VISIBLE);
            selectJobBtn.setText(R.string.select_job_label);
            clickable.setBackgroundResource(android.R.color.transparent);
            jobName.setTextColor(getResources().getColor(R.color.black_color));
            jobDate.setTextColor(getResources().getColor(R.color.black_color));
        }

        if(jobObject.id != -1) {
            jobInfo.setVisibility(VISIBLE);
            jobInfo.setJob(jobObject);
        }else
            jobInfo.setVisibility(GONE);

        if (!jobObject.expanded) {
            if (jobObject.baseHeight == 0f) {
                jobInfoContainer.post(() -> {
                    jobObject.baseHeight = jobInfoContainer.getHeight();
                    AnimationHelper.collapseAnimations(jobInfoContainer, 0, 0);
                    collapsed = true;
                    jobObject.expanded = false;
                });
            } else {
                AnimationHelper.collapseAnimations(jobInfoContainer, 0, 0);
                collapsed = true;
                jobObject.expanded = false;
            }
        }
//        }else{
//            jobInfo.setVisibility(GONE);
//            if (!jobObject.expanded) {
//                if (jobObject.baseHeight == 0f) {
//                    jobInfoContainer.post(() -> {
//                        jobObject.baseHeight = jobInfoContainer.getHeight();
//                        AnimationHelper.collapseAnimations(jobInfoContainer, 0, 0);
//                        collapsed = true;
//                        jobObject.expanded = false;
//                    });
//                } else {
//                    AnimationHelper.collapseAnimations(jobInfoContainer, 0, 0);
//                    collapsed = true;
//                    jobObject.expanded = false;
//                }
//            }
//        }
    }

    @Click
    protected void selectJobBtn(){
        if(jobSelectCallback != null)
            if(jobSelectCallback.getSelectedJobId() != null && jobObject.id == jobSelectCallback.getSelectedJobId()) {
                jobSelectCallback.onJobUnselect();
                fillView();
            }else{
                jobSelectCallback.onJobSelected(jobObject);
                fillView();
            }
    }
}
