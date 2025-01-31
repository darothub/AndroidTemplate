package com.anapfoundation.volunteerapp.data.repositories.auth

import com.anapfoundation.volunteerapp.model.*
import com.anapfoundation.volunteerapp.model.response.TopicResponse
import com.anapfoundation.volunteerapp.model.DefaultResponse
import com.anapfoundation.volunteerapp.model.request.AddReportResponse
import com.anapfoundation.volunteerapp.model.response.Reports
import com.anapfoundation.volunteerapp.model.user.UserResponse
import com.anapfoundation.volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.volunteerapp.services.authservices.AuthApiRequests
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
        zone:String?,
        suggestion:String?,
        header: String
    ): Call<AddReportResponse> {
        return authApiRequests.addReport(topic, rating, story, state, mediaURL, localGovernment, district, town, zone, suggestion, header)
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

    override fun getReports(header: String, first: Long?, after: Long?): Call<Reports> {
        return authApiRequests.getReports(header, first, after)
    }


    override fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        houseNumber: String?,
        state: String?,
        street: String?,
        localGovernment: String?,
        zone: String?,
        profileImageUrl:String?,
        header: String
    ): Call<ProfileData> {
        return authApiRequests.updateProfile(firstName, lastName, email, phone, houseNumber, state, street, localGovernment, zone, profileImageUrl, header)
    }

    override fun resetPassword(newPassword: String, token: String): Call<UserResponse> {
        return authApiRequests.resetPassword(newPassword, token)
    }

    override fun approveReport(id: String, header: String): Call<DefaultResponse> {
        return authApiRequests.approveReport(id, header)
    }

    override fun dismissReport(id: String, header: String): Call<DefaultResponse> {
        return authApiRequests.dismissReport(id, header)
    }

//    override fun getUnapprovedReports(header: String, first: Long?, after: Long?): Call<Reports> {
//        return authApiRequests.getUnapprovedReports(header, first, after)
//    }


}