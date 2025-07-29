package com.example.pachaapp.clas;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/firebase/register")
    Call<JsonObject> registerWithFirebase(@Body FirebaseUserRequest userData);

    @POST("auth/firebase/login")
    Call<JsonObject> loginWithFirebase(@Body FirebaseUserRequest userData);

    // Endpoints de actividades
    @FormUrlEncoded
    @POST("activity/create")
    Call<ApiResponse<Activity>> createActivity(
            @Field("idUsuario") String idUsuario,
            @Field("descripcion") String descripcion,
            @Field("lugar") String lugar,
            @Field("fechaActividad") long fechaActividad
    );

    @FormUrlEncoded
    @PUT("activity/update/{idActividad}")
    Call<ApiResponse<Activity>> updateActivity(
            @Path("idActividad") String idActividad,
            @Field("idUsuario") String idUsuario,
            @Field("descripcion") String descripcion,
            @Field("lugar") String lugar,
            @Field("estado") String estado,
            @Field("fechaActividad") Long fechaActividad
    );

    @GET("activity/get/{idActividad}")
    Call<ApiResponse<Activity>> getActivityById(@Path("idActividad") String idActividad);

    @GET("activity/user/{idUsuario}")
    Call<ApiResponse<List<Activity>>> getActivitiesByUser(@Path("idUsuario") String idUsuario);

    @GET("activity/user/{idUsuario}/status/{estado}")
    Call<ApiResponse<List<Activity>>> getActivitiesByUserAndStatus(
            @Path("idUsuario") String idUsuario,
            @Path("estado") String estado
    );

    @FormUrlEncoded
    @PUT("activity/complete/{idActividad}")
    Call<ApiResponse<Activity>> markActivityAsCompleted(
            @Path("idActividad") String idActividad,
            @Field("idUsuario") String idUsuario
    );

    @DELETE("activity/delete/{idActividad}")
    Call<ApiResponse<String>> deleteActivity(
            @Path("idActividad") String idActividad,
            @Query("idUsuario") String idUsuario
    );
}