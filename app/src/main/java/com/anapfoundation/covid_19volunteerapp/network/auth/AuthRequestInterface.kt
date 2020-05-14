package com.anapfoundation.covid_19volunteerapp.network.auth

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import retrofit2.Call

interface AuthRequestInterface {

    fun addReport(topic:String,
                  rating:String,
                  story:String,
                  state:String,
                  mediaURL:String?,
                  localGovernment:String?,
                  district:String?,
                  town:String?,
                  header:String): Call<DefaultResponse>

    fun getTopic(header: String): Call<TopicResponse>

    fun getRating(topicID:String, header: String): Call<TopicResponse>

    fun getProfileData(header: String):Call<ProfileData>

    fun getReports(header: String):Call<Reports>

    fun updateProfile(
        firstName:String, lastName:String, email: String, phone: String,
        houseNumber:String,  state:String?, street: String, header:String
    ):Call<ProfileData>
}