package com.flightpathcore.objects;

import android.database.Cursor;

import com.flightpathcore.objects.jobs.LegalWordObject;
import com.flightpathcore.objects.jobs.LooseItem;
import com.flightpathcore.objects.jobs.VehicleObject;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class JobObject {
    public long id;
    public String name;
    @SerializedName("loose_items")
    public List<LooseItem> looseItems;
    @SerializedName("yes_no_questions")
    public List<String> yesNoQuestions;
    @SerializedName("legal_words")
    public List<LegalWordObject> legalWords;
    public String date;
    @SerializedName("date_time")
    public String fullDate;
    public String type;
    public VehicleObject vehicle;
    public BookingObject booking;
    public boolean isJobDone = false;
    @SerializedName("extra_notes")
    public String extraNotes;
    public String notes;
    @SerializedName("abort_code")
    public String abortCode;
    @SerializedName("date_time_at")
    public String dateFrom;
    @SerializedName("date_time_to")
    public String dateTo;
    public String number;
    public String address;
    public String postcode;
    public String description;
    public String loan;
    public String street, street2;
    public String city;

    public String insurer;
    public String vatstatus;
    @SerializedName("customer_name")
    public String customerName;
    @SerializedName("courtesy_car")
    public boolean courtesyCar;
    @SerializedName("work_phone")
    public String workPhone;
    @SerializedName("home_phone")
    public String homePhone;
    @SerializedName("mobile_phone")
    public String mobile;
    public String excess;


    public boolean[] selectedLooseItems;
    public boolean expanded = false;
    public float baseHeight = 0f;
    @SerializedName("home_number")
    public String homeNumber = null;
    @SerializedName("ref_number")
    public String referenceNumber;

    public JobObject(){

    }

    public JobObject(Cursor cursor) {
        JobObject job = new Gson().fromJson(cursor.getString(8), this.getClass());
        id = job.id;
        name = job.name;
        looseItems = job.looseItems;
        yesNoQuestions = job.yesNoQuestions;
        legalWords = job.legalWords;
        date = job.date;
        fullDate = job.fullDate;
        type = job.type;
        vehicle = job.vehicle;
        isJobDone = job.isJobDone;
        extraNotes = job.extraNotes;
        notes = job.notes;
        abortCode = job.abortCode;
        dateFrom = job.dateFrom;
        dateTo = job.dateTo;
        number = job.number;
        address = job.address;
        postcode = job.postcode;
        description = job.description;
        loan = job.loan;
        city = job.city;
        street = job.street;
        insurer = job.insurer;
        vatstatus = job.vatstatus;
        customerName = job.customerName;
        courtesyCar = job.courtesyCar;
        workPhone = job.workPhone;
        homePhone = job.homePhone;
        excess = job.excess;
        homeNumber = job.homeNumber;
        street2 = job.street2;
        referenceNumber = job.referenceNumber;
        mobile = job.mobile;
    }

}
