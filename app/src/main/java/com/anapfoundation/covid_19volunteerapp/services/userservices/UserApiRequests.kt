package com.anapfoundation.covid_19volunteerapp.services.userservices

import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApiRequests {
    @POST("signup")
    @FormUrlEncoded
    fun registerUser(
        @Field("firstName") first:String,
        @Field("lastName") last:String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("password") password:String,
        @Field("houseNumber") houseNumber:String,
        @Field("street") street:String,
        @Field("state") state:String,
        @Field("localGovernment") localGovernment:String,
        @Field("zone") zone:String,
        @Field("district") district:String
    ): Call<UserResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginRequest(
        @Field("username") email:String,
        @Field("password") password:String
    ):Call<UserResponse>

    @GET("states")
    fun getStates(
        @Query("first") first: String,
        @Query("after") after: String?
    ): Call<StatesList>

    @GET("states/{stateID}/local")
    fun getLGA(
        @Path("stateID") stateID: String,
        @Query("first") first: String,
        @Query("after") after: String?
    ): Call<LGA>

    @POST("forgot-password")
    @FormUrlEncoded
    fun forgotPasswordRequest(@Field("email") email: String):Call<DefaultResponse>

    @GET("states/{stateID}")
    fun getSingleState(
        @Path("stateID") stateID: String
    ): Call<Location>

    @GET("local-governments/{lgaID}")
    fun getSingLGA(@Path("lgaID") lgaID: String): Call<Location>
}