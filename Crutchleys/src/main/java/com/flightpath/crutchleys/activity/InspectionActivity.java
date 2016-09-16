package com.flightpath.crutchleys.activity;

import android.content.Intent;
import android.os.Bundle;

import com.flightpath.crutchleys.R;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.objects.InspectionStructureResponse;
import com.flightpathcore.objects.JobObject;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.InspectionContainerFragment;
import flightpath.com.inspectionmodule.InspectionModuleInterfaces;
import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 15.06.2016.
 */
@EActivity(R.layout.activity_inspection)
public class InspectionActivity extends CrBaseActivity implements InspectionModuleInterfaces.InspectionListener {

    @FragmentByTag
    protected InspectionContainerFragment inspectionContainerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init() {
        InspectionStructureResponse structureResponse = buildStep1Inspection();
        structureResponse.widgetsStep2 = buildStep2Inspection();
        inspectionContainerFragment.buildInspection(structureResponse);
    }

    private InspectionStructureResponse buildStep1Inspection() {
        InspectionStructureResponse inspStructure;
        inspStructure = new Gson().fromJson(readRawJson(), InspectionStructureResponse.class);

        return inspStructure;
    }

    private String readRawJson(){
        InputStream is = null /*getResources().openRawResource(R.raw.inspection)*/;
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    private List<BaseWidgetObject> buildStep2Inspection() {
        List<BaseWidgetObject> list = new ArrayList<>();
        list.add(new CheckBoxObject(null, getString(R.string.sign_behalf_of_text), false, false, false));
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inspectionContainerFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!inspectionContainerFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onCompleteListener() {
        finish();
    }

    @Override
    public List<JobObject> getJobs() {
        return (List<JobObject>) DBHelper.getInstance().getMultiple(new JobsTable(), null);
    }
}