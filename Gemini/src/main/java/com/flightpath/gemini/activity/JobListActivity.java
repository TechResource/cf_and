package com.flightpath.gemini.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.flightpath.gemini.R;
import com.flightpathcore.adapters.ListAdapter;
import com.flightpathcore.adapters.ViewWrapper;
import com.flightpathcore.base.BaseActivity;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.widgets.SimpleDividerItemDecoration;
import com.flightpathcore.widgets.SingleJobWidget;
import com.flightpathcore.widgets.SingleJobWidget_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 20.05.2016.
 */
@EActivity(R.layout.activity_job_list)
public class JobListActivity extends GeminiBaseActivity implements BaseActivity.JobSelectCallback{

    @FragmentByTag
    protected HeaderFragment headerFragment;
    @ViewById
    protected RecyclerView recycler;
    @Inject
    protected TripStatusHelper tripStatusHelper;

    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
        headerFragment.setViewType(HeaderFragment.ViewType.CLEAR);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        adapter = new Adapter();
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setItems((List<JobObject>) DBHelper.getInstance().getMultiple(new JobsTable(),null));
    }

    @Override
    public void onJobSelected(JobObject jobObject) {
        adapter.notifyDataSetChanged();
        tripStatusHelper.setSelectedJob(this, jobObject);
    }

    @Override
    public void onJobUnselect() {
        adapter.notifyDataSetChanged();
        tripStatusHelper.setSelectedJob(this, null);
    }

    @Override
    public Long getSelectedJobId() {
        JobObject selectedJob = tripStatusHelper.getSelectedJob(this);
        if(selectedJob != null)
            return selectedJob.id;
        else
            return null;
    }

    public class Adapter extends ListAdapter<View, JobObject>{

        @Override
        protected View onCreateItemView(ViewGroup parent, int viewType) {
            return SingleJobWidget_.build(JobListActivity.this);
        }

        @Override
        public void onBindViewHolder(ViewWrapper<View> holder, int position) {
            ((SingleJobWidget)holder.getView()).setData(items.get(position), JobListActivity.this);
        }
    }

}
