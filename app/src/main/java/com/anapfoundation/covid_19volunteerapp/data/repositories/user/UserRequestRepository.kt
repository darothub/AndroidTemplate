package com.anapfoundation.covid_19volunteerapp.data.repositories.user

import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.userservices.UserApiRequests
import retrofit2.Call
import javax.inject.Inject

class UserRequestRepository @Inject constructor(val userApiRequests: UserApiRequests):UserRequestInterface {
    override fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String
    ): Call<ServiceResult> {
        return userApiRequests.registerUser(firstName, lastName, email, phone, password)
    }
}