package com.anapfoundation.covid_19volunteerapp.data.repositories.user

import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.userservices.UserApiRequests
import retrofit2.Call
import javax.inject.Inject

class UserRequestRepository @Inject constructor(val userApiRequests: UserApiRequests) :
    UserRequestInterface {
    override fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        houseNumber: String,
        street: String,
        state: String,
        localGovernment: String,
        zone:String,
        district: String
    ): Call<UserResponse> {
        return userApiRequests.registerUser(
            firstName,
            lastName,
            email,
            phone,
            password,
            houseNumber,
            street,
            state,
            localGovernment,
            zone,
            district
        )
    }

    override fun loginRequest(username: String, password: String): Call<UserResponse> {
        return userApiRequests.loginRequest(username, password)
    }

    override fun getStates(first: String, after: String?): Call<StatesList> {
        return userApiRequests.getStates(first, after)
    }

    override fun getLocal(stateID: String, first: String, after: String?): Call<LGA> {
        return userApiRequests.getLGA(stateID,first, after)
    }

    override fun forgotPasswordRequest(email: String): Call<DefaultResponse> {
        return userApiRequests.forgotPasswordRequest(email)
    }

    override fun getSingleLGA(lgaID: String): Call<Location> {
        return userApiRequests.getSingLGA(lgaID)
    }
}