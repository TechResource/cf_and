package com.flightpathcore.objects.jobs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-01-13.
 */
public class VehicleObject {
    @SerializedName("model_id")
    public int modelId;
    public int id;
    public String registration;
    @SerializedName("manufacturer_id")
    public int manufacturerId;
    public VehicleModelObject model;
    public VehicleManufacturerObject manufacturer;
}
