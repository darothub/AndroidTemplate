package com.anapfoundation.covid_19volunteerapp.services.authservices

import com.anapfoundation.covid_19volunteerapp.model.Report
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call
import retrofit2.http.*

interface AuthApiRequests {
    @POST("report")
    @FormUrlEncoded
    fun addReport(@Field("topic") topic:String,
                    @Field("rating") rating:String,
                    @Field("story") story:String,
                    @Field("state") state:String,
                    @Header("Authorization") header:String): Call<ServiceResult>
}