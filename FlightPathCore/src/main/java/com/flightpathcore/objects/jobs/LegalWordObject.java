package com.flightpathcore.objects.jobs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class LegalWordObject {
    public String content;
    @SerializedName("field_type")
    public String fieldType;
    public String placeholder;
    public int id;
}
