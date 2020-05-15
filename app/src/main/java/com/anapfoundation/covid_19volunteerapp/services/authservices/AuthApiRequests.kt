package com.anapfoundation.covid_19volunteerapp.services.authservices

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.request.AddReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import retrofit2.Call
import retrofit2.http.*

interface AuthApiRequests {
    @POST("reports")
    @FormUrlEncoded
    fun addReport(
        @Field("topic") topic: String,
        @Field("rating") rating: String,
        @Field("story") story: String,
        @Field("state") state: String,
        @Field("mediaURL") mediaURL: String?,
        @Field("localGovernment") lga: String?,
        @Field("district") district:String?,
        @Field("town") street: String?,
        @Field("suggestion") suggestion:String?,
        @Header("Authorization") header: String
    ): Call<AddReportResponse>

    @GET("topics")
    fun getTopic(@Header("Authorization") header: String): Call<TopicResponse>

    @GET("topics/ratings/{topicID}")
    fun getRating(
        @Path("topicID") topicID: String,
        @Header("Authorization") header: String
    ): Call<TopicResponse>

    @GET("me")
    fun getProfileData(@Header("Authorization") header: String): Call<ProfileData>

    @GET("reports")
    fun getReportss(@Header("Authorization") header: String, @Query("first") first: Long?,
                    @Query("after") after: Long?): Call<Reports>

    @GET("reports")
    fun getReports(@Header("Authorization") header: String, @Query("first") first: Long?,
                   @Query("after") after: Long?): Call<Reports>

    @POST("me")
    @FormUrlEncoded
    fun updateProfile(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("houseNumber") houseNumber: String?,
        @Field("state") state: String?,
        @Field("street") street: String?,
        @Field("profile_image_url") profileImageUrl:String?,
        @Header("Authorization") header: String?
    ): Call<ProfileData>

    @PUT("reset-password")
    @FormUrlEncoded
    fun resetPassword(@Field("newPassword") newPassword:String, @Field("token") token: String):Call<DefaultResponse>

    @GET("review/reports")
    fun getUnapprovedReports(@Header("Authorization") header: String, @Query("first") first: Long?,
                             @Query("after") after: Long?): Call<Reports>
    @PUT("review/reports")
    @FormUrlEncoded
    fun approveReport(@Field("id") id:String, @Header("Authorization") header: String):Call<DefaultResponse>
}
