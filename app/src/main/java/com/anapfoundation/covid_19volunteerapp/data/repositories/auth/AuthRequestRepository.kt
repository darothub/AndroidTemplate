package com.anapfoundation.covid_19volunteerapp.data.repositories.auth

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import retrofit2.Call
import javax.inject.Inject

class AuthRequestRepository @Inject constructor(val authApiRequests: AuthApiRequests):AuthRequestInterface {
    override fun addReport(
        topic: String,
        rating: String,
        story: String,
        state: String,
        header: String
    ): Call<ServiceResult> {
        return authApiRequests.addReport(topic, rating, story, state, header)
    }

    override fun getTopic(header: String): Call<TopicData> {
        return authApiRequests.getTopic(header)
    }

    override fun getRating(topicID: String, header: String): Call<TopicData> {
        return authApiRequests.getRating(topicID, header)
    }

    override fun getStates(header: String): Call<StatesList> {
        return authApiRequests.getStates(header)
    }

    override fun getProfileData(header: String): Call<ProfileData> {
        return authApiRequests.getProfileData(header)
    }


}