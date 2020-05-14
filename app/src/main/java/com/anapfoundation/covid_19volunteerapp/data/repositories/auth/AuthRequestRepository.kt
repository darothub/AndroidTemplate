package com.anapfoundation.covid_19volunteerapp.data.repositories.auth

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.request.AddReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import retrofit2.Call
import javax.inject.Inject

class AuthRequestRepository @Inject constructor(val authApiRequests: AuthApiRequests):AuthRequestInterface  {
    override fun addReport(
        topic: String,
        rating: String,
        story: String,
        state: String,
        mediaURL:String?,
        localGovernment:String?,
        district:String?,
        town:String?,
        header: String
    ): Call<AddReportResponse> {
        return authApiRequests.addReport(topic, rating, story, state, mediaURL, localGovernment, district, town, header)
    }

    override fun getTopic(header: String): Call<TopicResponse> {
        return authApiRequests.getTopic(header)
    }

    override fun getRating(topicID: String, header: String): Call<TopicResponse> {
        return authApiRequests.getRating(topicID, header)
    }


    override fun getProfileData(header: String): Call<ProfileData> {
        return authApiRequests.getProfileData(header)
    }

    override fun getReports(header: String): Call<Reports> {
        return authApiRequests.getReports(header)
    }

    override fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        houseNumber: String,
        state: String?,
        street: String,
        header: String
    ): Call<ProfileData> {
        return authApiRequests.updateProfile(firstName, lastName, email, phone, houseNumber, state, street, header)
    }




}