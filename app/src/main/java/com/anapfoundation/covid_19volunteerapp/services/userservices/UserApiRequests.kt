package com.anapfoundation.covid_19volunteerapp.services.userservices

import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApiRequests {
    @POST("signup")
    @FormUrlEncoded
    fun registerUser(
        @Field("firstName") first:String,
        @Field("lastName") last:String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("password") password:String

    ): Call<ServiceResult>
}