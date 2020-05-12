package com.anapfoundation.covid_19volunteerapp.network.user

import com.anapfoundation.covid_19volunteerapp.model.LocalGovernment
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call


interface UserRequestInterface {

    fun registerUser(firstName:String, lastName:String, email: String, password: String, phone: String,
                     houseNumber:String, street:String, state:String, localGovernment: String, district:String): Call<ServiceResult> {
        return TODO()
    }

    fun loginRequest(username:String, password: String): Call<ServiceResult> {
        return TODO()
    }
}