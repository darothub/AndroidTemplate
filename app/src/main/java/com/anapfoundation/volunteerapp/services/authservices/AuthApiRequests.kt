package com.anapfoundation.volunteerapp.services.authservices

import com.anapfoundation.volunteerapp.model.*
import com.anapfoundation.volunteerapp.model.response.TopicResponse
import com.anapfoundation.volunteerapp.model.DefaultResponse
import com.anapfoundation.volunteerapp.model.request.AddReportResponse
import com.anapfoundation.volunteerapp.model.response.Reports
import com.anapfoundation.volunteerapp.model.user.UserResponse
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
        @Field("mediaURL") mediaURL: String?=null,
        @Field("localGovernment") lga: String?,
        @Field("district") district:String?,
        @Field("town") street: String?,
        @Field("zone") zone: String?,
        @Field("suggestion") suggestion:String?=null,
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
    fun getReportAfter(@Header("Authorization") header: String, @Query("first") first: Long?,
                    @Query("after") after: Long?): Call<Reports>

    @GET("reports")
    fun getReport(@Header("Authorization") header: String, @Query("first") first: Long?): Call<Reports>

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
        @Field("house_number") houseNumber: String?=null,
        @Field("state") state: String?,
        @Field("localGovernment") localGovernment: String?,
        @Field("street") street: String?,
        @Field("zone") zone: String?,
        @Field("profile_image_url") profileImageUrl:String?=null,
        @Header("Authorization") header: String?
    ): Call<ProfileData>

    @PUT("reset-password")
    @FormUrlEncoded
    fun resetPassword(@Field("newPassword") newPassword:String, @Field("token") token: String):Call<UserResponse>

    @GET("review/reports")
    fun getUnapprovedReports(@Header("Authorization") header: String, @Query("first") first: Long?): Call<Reports>

    @GET("review/reports")
    fun getUnapprovedReportsAfter(@Header("Authorization") header: String, @Query("first") first: Long?,
                             @Query("after") after: Long?): Call<Reports>

    @GET("review/reports/approved")
    fun getApprovedReports(@Header("Authorization") header: String, @Query("first") first: Long?): Call<Reports>

    @GET("review/reports/approved")
    fun getApprovedReportsAfter(@Header("Authorization") header: String, @Query("first") first: Long?,
                                  @Query("after") after: Long?): Call<Reports>

    @PUT("review/reports/approve")
    @FormUrlEncoded
    fun approveReport(@Field("id") id:String, @Header("Authorization") header: String):Call<DefaultResponse>

    @PUT("review/reports/dismiss")
    @FormUrlEncoded
    fun dismissReport(@Field("id") id:String, @Header("Authorization") header: String):Call<DefaultResponse>
}
