package com.flightpathcore.network;

import com.flightpathcore.network.requests.JobsRequest;
import com.flightpathcore.network.requests.LoginRequest;
import com.flightpathcore.network.requests.SynchronizeRequest;
import com.flightpathcore.objects.JobObject;
import com.flightpathcore.objects.PeriodResponse;
import com.flightpathcore.objects.UpdateAppObject;
import com.flightpathcore.objects.UserObject;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2015-10-20.
 */
public interface FPApi {
    @POST("/session/login")
    void login(@Body LoginRequest loginRequest, MyCallback<UserObject> callback);

//    @Multipart
//    @PUT("/reports/report")
//    void uploadLogFile(@Part("token_id")int tokenId,
//                              @Part("access")String access,
//                              @Part("date")String date,
//                              @Part("mobile_log")TypedFile file,
//                              Callback<Object> callback);
//
//    @POST("/session/refresh")
//    UserDataObject refreshToken(@Body RefreshTokenRequest baseRequest /*, MyCallback<UserDataObject> callback*/);
//
//    @POST("/session/logout")
//    void logout(@Body BaseRequest baseRequest, MyCallback<Object> callback);
//
    @POST("/driver/synchronize")
    Integer sendEvents(@Body SynchronizeRequest request/*, MyCallback<Object> callback*/); //remove callback and make request synchronous
//
//    @POST("/driver/synchronize")
//    void sendEventsAsync(@Body SynchronizeRequest request, MyCallback<Object> callback); //remove callback and make request synchronous
//
//    @Multipart
//    @POST("/driver/files")
//    Object sendMultipleFiles(@PartMap Map<String,TypedFile> Files);

    @POST("/driver/jobs")
    void getJobs(@Body JobsRequest request, MyCallback<List<JobObject>> callback);

    @GET("/driver/check_update")
    void checkUpdate(MyCallback<UpdateAppObject> callback);

    @GET("/driver/period_trips")
    void periodTrips(Callback<PeriodResponse> callback);

    @GET("/drivers/closePeriodByToken")
    void closePeriod(@Query("token")String token, @Query("end_date")String endDate,@Query("end_mileage")String endMileage,
                     @Query("agent_email")String agentEmail, Callback<Object> callback);

}
