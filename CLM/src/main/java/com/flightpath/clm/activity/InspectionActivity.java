package com.flightpath.clm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.flightpath.clm.CLMApplication;
import com.flightpath.clm.R;
import com.flightpathcore.database.DBHelper;
import com.flightpathcore.database.tables.JobsTable;
import com.flightpathcore.objects.BaseWidgetObject;
import com.flightpathcore.objects.InspectionStructureResponse;
import com.flightpathcore.objects.JobObject;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;

import java.util.ArrayList;
import java.util.List;

import flightpath.com.inspectionmodule.InspectionContainerFragment;
import flightpath.com.inspectionmodule.InspectionModuleInterfaces;
import flightpath.com.inspectionmodule.widgets.objects.CheckBoxObject;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-21.
 */
@EActivity(R.layout.activity_inspection)
public class InspectionActivity extends CLMBaseActivity implements InspectionModuleInterfaces.InspectionListener {

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
        List<BaseWidgetObject> list = new ArrayList<>();
        InspectionStructureResponse inspStructure;
        //CLM 1
        if(!((CLMApplication)getApplication()).isCLM2(this)) {
            inspStructure = new Gson().fromJson("{\"inspection\":[{\"type\":\"spinner\",\"property\":\"jobId\",\"hint\":null,\"value\":" +
                    "\"jobs\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"spinner\",\"property\":\"jobType\",\"hint\":null,\"value\":" +
                    "\"job_type\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":\"refNumber\",\"hint\":" +
                    "\"Reference Number\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "\"customerName\",\"hint\":\"Customer Name\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "\"mobile\",\"hint\":\"Mobile\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":"+
                    "\"customer_phone\",\"hint\":\"Home Phone\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "\"workPhone\",\"hint\":\"Work Phone\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Address\",\"is_required\":null,\"is_editable\":null,\"input_type\":" +
                    "null},{\"type\":\"input\",\"property\":\"homeNumber\",\"hint\":\"Home Name/Number\",\"value\":null,\"is_required\":true,\"is_editable\":" +
                    "true,\"input_type\":null},{\"type\":\"input\",\"property\":\"addressLine1\",\"hint\":\"Address Line 1\",\"value\":null,\"is_required\":" +
                    "false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"addressLine2\",\"hint\":\"Address Line 2\",\"value\":" +
                    "null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"city\",\"hint\":\"City\",\"value\":" +
                    "null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"postcode\",\"hint\":\"PostCode\",\"value\":" +
                    "null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":" +
                    "\"Vehicle\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":registration,\"hint\":" +
                    "\"Registration\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "manufacturer,\"hint\":\"Manufacturer\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"input\",\"property\":model,\"hint\":\"Model\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Damages\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":" +
                    "\"damages\",\"property\":\"damagedItems\",\"hint\":null,\"value\":null,\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":" +
                    "\"input\",\"property\":\"mileage\",\"hint\":\"Mileage\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":" +
                    "\"number\"},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Loose Items\",\"is_required\":null,\"is_editable\":" +
                    "null,\"input_type\":null},{\"type\":\"loose_items\",\"property\":\"looseItemsChecked\",\"hint\":null,\"value\":null,\"is_required\":" +
                    "null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":\"driverNotes\",\"hint\":\"Notes\",\"value\":null,\"is_required\":" +
                    "false,\"is_editable\":true,\"input_type\":null},{\"type\":\"check_box\",\"property\":\"includeCustomerSignature\",\"hint\":" +
                    "\"I would like to include Customer signature\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null}]}", InspectionStructureResponse.class);
        }else {
            //CLM 2
            inspStructure = new Gson().fromJson("{\"inspection\":[{\"type\":\"spinner\",\"property\":\"jobId\",\"hint\":null,\"value\":" +
                    "\"jobs\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"spinner\",\"property\":\"jobType\",\"hint\":null,\"value\":" +
                    "\"job_type\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":\"refNumber\",\"hint\":" +
                    "\"Reference Number\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "\"customerName\",\"hint\":\"Customer Name\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Address\",\"is_required\":null,\"is_editable\":null,\"input_type\":" +
                    "null},{\"type\":\"input\",\"property\":\"homeNumber\",\"hint\":\"Home Name/Number\",\"value\":null,\"is_required\":false,\"is_editable\":" +
                    "true,\"input_type\":null},{\"type\":\"input\",\"property\":\"addressLine1\",\"hint\":\"Address Line 1\",\"value\":null,\"is_required\":" +
                    "false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"addressLine2\",\"hint\":\"Address Line 2\",\"value\":" +
                    "null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"city\",\"hint\":\"City\",\"value\":" +
                    "null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":\"postcode\",\"hint\":\"PostCode\",\"value\":" +
                    "null,\"is_required\":false,\"is_editable\":true,\"input_type\":null},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":" +
                    "\"Vehicle\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":registration,\"hint\":" +
                    "\"Registration\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":\"input\",\"property\":" +
                    "manufacturer,\"hint\":\"Manufacturer\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"input\",\"property\":model,\"hint\":\"Model\",\"value\":null,\"is_required\":true,\"is_editable\":true,\"input_type\":null},{\"type\":" +
                    "\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Damages\",\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":" +
                    "\"damages\",\"property\":\"damagedItems\",\"hint\":null,\"value\":null,\"is_required\":null,\"is_editable\":null,\"input_type\":null},{\"type\":" +
                    "\"input\",\"property\":\"mileage\",\"hint\":\"Mileage\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":" +
                    "\"number\"},{\"type\":\"section_header\",\"property\":null,\"hint\":null,\"value\":\"Loose Items\",\"is_required\":null,\"is_editable\":" +
                    "null,\"input_type\":null},{\"type\":\"loose_items\",\"property\":\"looseItemsChecked\",\"hint\":null,\"value\":null,\"is_required\":" +
                    "null,\"is_editable\":null,\"input_type\":null},{\"type\":\"input\",\"property\":\"driverNotes\",\"hint\":\"Notes\",\"value\":null,\"is_required\":" +
                    "false,\"is_editable\":true,\"input_type\":null},{\"type\":\"check_box\",\"property\":\"includeCustomerSignature\",\"hint\":" +
                    "\"I would like to include Customer signature\",\"value\":null,\"is_required\":false,\"is_editable\":true,\"input_type\":null}]}", InspectionStructureResponse.class);
        }

        return inspStructure;
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
