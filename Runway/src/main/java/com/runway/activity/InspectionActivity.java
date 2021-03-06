package com.runway.activity;

import android.content.Intent;
import android.os.Bundle;

import com.flightpathcore.objects.InspectionStructureResponse;
import com.flightpathcore.objects.InspectionWidgetTypes;
import com.flightpathcore.utilities.SPHelper;
import com.google.gson.Gson;
import com.runway.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.InspectionContainerFragment;

import com.flightpathcore.objects.BaseWidgetObject;
import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;
import flightpath.com.inspectionmodule.widgets.objects.DamagesObject;
import flightpath.com.inspectionmodule.widgets.objects.InputObject;
import flightpath.com.inspectionmodule.widgets.objects.LooseItemsObject;
import flightpath.com.inspectionmodule.widgets.objects.SectionHeaderObject;
import flightpath.com.inspectionmodule.widgets.objects.SpinnerObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-14.
 */
@EActivity(R.layout.activity_inspection)
public class InspectionActivity extends RunwayBaseActivity {

    @FragmentByTag
    protected InspectionContainerFragment inspectionContainerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        di().inject(this);
    }

    @AfterViews
    protected void init(){
//        inspectionContainerFragment.buildInspection(buildStep1Inspection(), buildStep2Inspection());
    }

    private InspectionStructureResponse buildStep1Inspection() {
        List<BaseWidgetObject> list = new ArrayList<>();
//        InspectionStructureResponse inspStructure = new Gson().fromJson(SPHelper.getData(this, SPHelper.INSPECTION_STRUCTURE), InspectionStructureResponse.class);
        //temporary
        InspectionStructureResponse inspStructure = new Gson().fromJson("{\"inspection\":[{\"type\":\"spinner\",\"property\":\"jobId\",\"hint\":null,\"value\":\"jobs\",\"is_required\":" +
                "null,\"is_editable\":null},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Vehicle\",\"is_required\":null,\"is_editable\":null},{\"type\":\"" +
                "input\",\"property\":null,\"hint\":\"Registration\",\"value\":null,\"is_required\":false,\"is_editable\":false},{\"type\":\"input\",\"property\":null,\"hint\":\"" +
                "Manufacturer\",\"value\":null,\"is_required\":false,\"is_editable\":false},{\"type\":\"input\",\"property\":null,\"hint\":\"Model\",\"value\":null,\"is_required\"" +
                ":false,\"is_editable\":false},{\"type\":\"input\",\"property\":null,\"hint\":\"Loan duration\",\"value\":null,\"is_required\":false,\"is_editable\":false},{\"type\"" +
                ":\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Damages\",\"is_required\":null,\"is_editable\":null},{\"type\":\"damages\",\"property\":null,\"hint\"" +
                ":null,\"value\":null,\"is_required\":null,\"is_editable\":null},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Loose Items\",\"is_required\"" +
                ":null,\"is_editable\":null},{\"type\":\"loose_items\",\"property\":null,\"hint\":null,\"value\":null,\"is_required\":null,\"is_editable\":null},{\"type\":\"input\",\"" +
                "property\":\"driverNotes\",\"hint\":\"Notes\",\"value\":null,\"is_required\":false,\"is_editable\":true},{\"type\":\"check_box\",\"property\":\"includeCustomerSignature" +
                "\",\"hint\":\"I would like to include Customer signature\",\"value\":null,\"is_required\":false,\"is_editable\":true},]}", InspectionStructureResponse.class);


//        list.add(new SpinnerObject("jobId", SpinnerObject.SpinnerType.JOBS, R.integer.view_tag_spinner_jobs));
//        list.add(new SectionHeaderObject("Vehicle"));
//        list.add(new InputObject(null,"Registration", null, R.integer.view_tag_registration, true, false));
//        list.add(new InputObject(null,"Manufacturer", null, R.integer.view_tag_manufacturer, true, false));
//        list.add(new InputObject(null,"Model", null, R.integer.view_tag_model, true, false));
//        list.add(new InputObject(null,"Loan Duration", null, R.integer.view_tag_loan_duration, true, false));
//        list.add(new SectionHeaderObject("Damages"));
//        list.add(new DamagesObject("damagedItems",R.integer.view_tag_damages));
//        list.add(new SectionHeaderObject("Loose Items"));
//        list.add(new LooseItemsObject("looseItemsChecked",R.integer.view_tag_loose_items));
//        list.add(new InputObject("driverNotes","Notes", null, R.integer.view_tag_notes, false, true));
//        list.add(new CheckBoxObject("includeCustomerSignature","I would like to include Customer signature", false, false, true));
        return inspStructure;
    }

    private List<BaseWidgetObject> buildStep2Inspection() {
        List<BaseWidgetObject> list = new ArrayList<>();
        //it's taken from job object
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inspectionContainerFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(!inspectionContainerFragment.onBackPressed()){
            super.onBackPressed();
        }
    }
}
