package com.anapfoundation.covid_19volunteerapp.network.user

import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import retrofit2.Call


interface UserRequestInterface {

    fun registerUser(firstName:String, lastName:String, email: String, password: String, phone: String,
                     houseNumber:String, street:String, state:String, localGovernment: String, zone:String, district:String): Call<UserResponse> {
        return TODO()
    }

    fun loginRequest(username:String, password: String): Call<UserResponse> {
        return TODO()
    }

    fun getStates(first: String, after:String?=""): Call<StatesList>

    fun getLocal(stateID:String, first: String, after: String?=""): Call<LGA>

    fun forgotPasswordRequest(email: String):Call<DefaultResponse>

    fun getSingleLGA(lgaID:String):Call<Location>
}