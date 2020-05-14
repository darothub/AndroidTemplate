package com.anapfoundation.covid_19volunteerapp.services.userservices

import android.provider.ContactsContract
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.StatesList
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
        @Field("district") district:String
    ): Call<DefaultResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginRequest(
        @Field("username") email:String,
        @Field("password") password:String
    ):Call<DefaultResponse>

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
}