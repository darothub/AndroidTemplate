package com.anapfoundation.covid_19volunteerapp.services.authservices

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call
import retrofit2.http.*

interface AuthApiRequests {
    @POST("report")
    @FormUrlEncoded
    fun addReport(
        @Field("topic") topic: String,
        @Field("rating") rating: String,
        @Field("story") story: String,
        @Field("state") state: String,
        @Field("mediaURL") mediaURL: String?,
        @Field("localGovernment") lga: String?,
        @Field("town") street: String?,
        @Header("Authorization") header: String
    ): Call<ServiceResult>

    @GET("topics")
    fun getTopic(@Header("Authorization") header: String): Call<TopicData>

    @GET("topics/ratings/{topicID}")
    fun getRating(
        @Path("topicID") topicID: String,
        @Header("Authorization") header: String
    ): Call<TopicData>

    @GET("states")
    fun getStates(
        @Query("first") first: String,
        @Query("after") after: String?
    ): Call<StatesList>

    @GET("states/{stateID}/local")
    fun getLGA(
        @Path("stateID") stateID: String
    ): Call<LGA>

    @GET("me")
    fun getProfileData(@Header("Authorization") header: String): Call<ProfileData>

}