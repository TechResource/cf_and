package com.flightpathcore.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-12-14.
 */
public class PeriodResponse {
    public String url;
    public Period period;
    public ArrayList<Trip> trips;
    @SerializedName("confirm_text")
    public String confirmText;

    public static class Period{
        public int id;
        @SerializedName("start_mileage")
        public Integer startMileage;
        @SerializedName("start_date")
        public String startDate;
        @SerializedName("closing_token")
        public String closingToken;
    }

    public static class Trip implements ListItem{
        public int id;
        @SerializedName("vehicle_reg_number")
        public String vehicleReg;
        @SerializedName("end_date")
        public String endDate;
        @SerializedName("start_mileage")
        public Integer startMileage;
        @SerializedName("start_date")
        public String startDate;
        @SerializedName("end_mileage")
        public Integer endMileage;
        @SerializedName("business_mileage")
        public Integer businessMileage;
        @SerializedName("private_mileage")
        public Integer privateMileage;
    }
}
