package com.flightpathcore.database.tables;

import android.content.ContentValues;

import com.flightpathcore.objects.JobObject;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class JobsTable implements AbstractTable<JobObject> {

    public static final String TABLE_NAME = "jobs";
    public static final String JOB_ID = "j_id";
    public static final String JOB_IS_FINISHED = "j_finished";
    public static final String JOB_NAME = "j_name";
    public static final String JOB_DATE = "j_date";
    public static final String JOB_DATE_SIMPLE = "j_date_simple";
    public static final String JOB_TYPE = "j_job_type";
    public static final String JOB_DRIVER_ID = "j_driver_id";
    public static final String JOB_ACKNOWLEDGED = "j_acknowledged";
    public static final String JOB_CONTENT = "j_content";


    public static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + " (" +
            JOB_ID + " integer primary key," +
            JOB_IS_FINISHED + " integer, " +
            JOB_NAME + " text, " +
            JOB_DATE + " text, " +
            JOB_DATE_SIMPLE + " text, " +
            JOB_TYPE + " text, " +
            JOB_DRIVER_ID + " integer, " +
            JOB_ACKNOWLEDGED + " integer, " +
            JOB_CONTENT + " text " +
            ");";

    private static final String[] allColumns = {JOB_ID, JOB_IS_FINISHED, JOB_NAME, JOB_DATE_SIMPLE,
            JOB_DATE_SIMPLE, JOB_TYPE, JOB_DRIVER_ID, JOB_ACKNOWLEDGED, JOB_CONTENT};

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getWhereClause(String id) {
        return id == null ? null : JOB_ID + " = " + id;
    }

    public String getWhereTodayNotFinishedJobs() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return JOB_DATE + " = '" + today + "' AND " + JOB_IS_FINISHED + "='0' ";
    }

    public String getWhereTodayNotFinishedJobsWithNoJob() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return JOB_DATE + " = '" + today + "' AND " + JOB_IS_FINISHED + "='0' OR "+JOB_ID+" = '-1'";
    }

    @Override
    public String getOutputClassName() {
        return JobObject.class.getName();
    }

    @Override
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    public String getIdColumn() {
        return JOB_ID;
    }

    Gson gson = new Gson();

    @Override
    public ContentValues getContentValues(JobObject object) {
        ContentValues values = new ContentValues();
        values.put(JOB_ID, object.id);
        values.put(JOB_IS_FINISHED, object.isJobDone ? 1 : 0);
        values.put(JOB_CONTENT, gson.toJson(object));
        values.put(JOB_DATE, object.date);
        return values;
    }

    public List<ContentValues> getMultipleValues(List<JobObject> jobs) {
        List<ContentValues> values = new ArrayList<>();
        for (JobObject j : jobs) {
            values.add(getContentValues(j));
        }
        return values;
    }
}
