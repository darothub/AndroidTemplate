package com.anapfoundation.covid_19volunteerapp.network.auth

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.request.AddReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import retrofit2.Call

interface AuthRequestInterface {

    fun addReport(topic:String,
                  rating:String,
                  story:String,
                  state:String,
                  mediaURL:String?="",
                  localGovernment:String?,
                  district:String?,
                  town:String?,
                  zone:String?,
                  suggestion:String?="",
                  header:String): Call<AddReportResponse>

    fun getTopic(header: String): Call<TopicResponse>

    fun getRating(topicID:String, header: String): Call<TopicResponse>

    fun getProfileData(header: String):Call<ProfileData>

    fun getReports(header: String, first:Long?, after:Long?):Call<Reports>

    fun updateProfile(
        firstName:String, lastName:String, email: String, phone: String,
        houseNumber:String?=null,  state:String?, street: String?, zone:String?, profileImageUrl:String?=null, header:String
    ):Call<ProfileData>

    fun resetPassword(newPassword:String, token: String):Call<DefaultResponse>

    fun approveReport(id:String, header: String):Call<DefaultResponse>
    
    fun dismissReport(id:String, header: String):Call<DefaultResponse>

//    fun getUnapprovedReports(header: String, first:Long?, after:Long?):Call<Reports>


}