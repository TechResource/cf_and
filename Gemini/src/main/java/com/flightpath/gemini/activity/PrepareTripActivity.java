package com.flightpath.gemini.activity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.flightpath.gemini.R;
import com.flightpathcore.adapters.BaseSpinnerAdapter;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.database.tables.TripTable;
import com.flightpathcore.fragments.HeaderFragment;
import com.flightpathcore.network.FPModel;
import com.flightpathcore.network.MyCallback;
import com.flightpathcore.network.requests.JobsRequest;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.TripObject;
import com.flightpathcore.utilities.SPHelper;
import com.flightpathcore.utilities.Utilities;
import com.flightpathcore.widgets.JobInfoWidget;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import flightpath.com.mapmodule.LocationHandler;
import flightpath.com.mapmodule.TripStatusHelper;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.activity_prepare_trip)
public class PrepareTripActivity extends GeminiBaseActivity implements HeaderFragment.HeaderCallback {

    @FragmentById
    protected HeaderFragment headerFragment;
    @ViewById
    protected EditText currentMileage;
    @ViewById
    protected Spinner jobsSpinner;
    @ViewById
    protected Button getJobs;
    @ViewById
    protected LinearLayout noJobsContainer, tripInfoContainer;
    @ViewById
    protected JobInfoWidget jobInfoWidget;
    private BaseSpinnerAdapter adapter;
    private List<JobObject> currentlyJobList = null;
    @Inject
    protected FPModel fpModel;
    @Inject
    protected LocationHandler locationHandler;
    @Inject
    protected TripStatusHelper tripStatusHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init() {
        headerFragment.setViewType(HeaderFragment.ViewType.PREPARE_TRIP);
        headerFragment.setHeaderCallback(this);
        fillViewWithJobs();
    }

    private void fillViewWithJobs() {
        JobsTable jobsTable = new JobsTable();
        Integer selectedIndex = 0;
        currentlyJobList = (List<JobObject>) DBHelper.getInstance().getMultiple(new JobsTable(), null, jobsTable.getWhereTodayNotFinishedJobsWithNoJob(), null, null);
        JobObject selectedJob = tripStatusHelper.getSelectedJob(this);
        if(selectedJob != null){
            for (int i=0;i<currentlyJobList.size();i++){
                if(currentlyJobList.get(i).id == selectedJob.id){
                    selectedIndex = i;
                }
            }
        }
        adapter = new BaseSpinnerAdapter(this, currentlyJobList);
        jobsSpinner.setAdapter(adapter);
        jobsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jobInfoWidget.setVisibility(View.VISIBLE);
                jobInfoWidget.setJob(currentlyJobList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(selectedIndex != null) {
            jobsSpinner.setSelection(selectedIndex);
            jobsSpinner.setEnabled(false);
        }

        if (currentlyJobList.size() == 0) {
            noJobsContainer.setVisibility(View.VISIBLE);
            tripInfoContainer.setVisibility(View.GONE);
        } else {
            noJobsContainer.setVisibility(View.GONE);
            tripInfoContainer.setVisibility(View.VISIBLE);
        }
    }

    @AfterTextChange
    protected void currentMileage() {
        headerFragment.setRightBtnEnabled(!currentMileage.getText().toString().isEmpty());
    }

    @Override
    public void onHeaderLeftBtnClick() {
        onBackPressed();
    }

    @Override
    public void onHeaderRightBtnClick() {
        int startMileage = -1;
        try {
            startMileage = Integer.valueOf(currentMileage.getText().toString());
        } catch (NumberFormatException e) {
            //should never happen
        }
        JobObject selectedJob = currentlyJobList.get(jobsSpinner.getSelectedItemPosition());
        TripObject newTrip = new TripObject(startMileage, selectedJob.id);

        Location l = locationHandler.getLocation();
        newTrip.startLat = l.getLatitude();
        newTrip.startLon = l.getLongitude();
        newTrip.startDateAsTimestamp = Utilities.getTimestamp();
        DBHelper.getInstance().insert(new TripTable(), new TripTable().getContentValues(newTrip));
//        newTrip.type = EventObject.EventType.START;
//        newTrip.timestamp = Utilities.getTimestamp();
//        DBHelper.getInstance().createEvent(newTrip);
        tripStatusHelper.startNewTrip(newTrip, l);

        finish();
    }

    @Override
    public void onMenuBtnClick() {

    }

    @Click
    protected void getJobs() {
        fpModel.fpApi.getJobs(new JobsRequest(SPHelper.getUserSession(this)), new MyCallback<List<JobObject>>() {
            @Override
            public void onSuccess(List<JobObject> response) {
                DBHelper.getInstance().insertMultiple(new JobsTable(), new JobsTable().getMultipleValues(response));
                fillViewWithJobs();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PrepareTripActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
