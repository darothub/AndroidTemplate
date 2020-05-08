package com.anapfoundation.covid_19volunteerapp.data.repositories.auth

import com.anapfoundation.covid_19volunteerapp.model.Report
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


}