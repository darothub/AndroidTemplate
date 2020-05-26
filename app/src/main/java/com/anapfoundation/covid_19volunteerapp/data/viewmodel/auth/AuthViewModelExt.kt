package com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun AuthViewModel.resetPassword(newPassword:String, token:String): LiveData<ServicesResponseWrapper<Data>> {
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    responseLiveData.value = ServicesResponseWrapper.Loading(
        null,
        "Loading..."
    )
    val request = authRequestInterface.resetPassword(newPassword, token)
    request.enqueue(object : Callback<DefaultResponse> {
        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
            onFailureResponse(responseLiveData, t)
        }

        override fun onResponse(
            call: Call<DefaultResponse>,
            response: Response<DefaultResponse>
        ) {
            onResponseTask(response as Response<Data>, responseLiveData)
        }

    })
    return responseLiveData
}

fun AuthViewModel.updateProfile(
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    houseNumber: String?=null,
    state: String,
    street: String?,
    zone:String?,
    profileImageUrl: String?=null,
    header: String
): LiveData<ServicesResponseWrapper<Data>> {
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    responseLiveData.value = ServicesResponseWrapper.Loading(
        null,
        "Loading..."
    )
    val request = authRequestInterface.updateProfile(
        firstName,
        lastName,
        email,
        phone,
        houseNumber,
        state,
        street,
        zone,
        profileImageUrl,
        header
    )
    request.enqueue(object : Callback<ProfileData> {
        override fun onFailure(call: Call<ProfileData>, t: Throwable) {
            onFailureResponse(responseLiveData, t)
        }

        override fun onResponse(
            call: Call<ProfileData>,
            response: Response<ProfileData>
        ) {
            onResponseTask(response as Response<Data>, responseLiveData)
        }

    })
    return responseLiveData
}

fun AuthViewModel.approveReport(id:String, header:String): LiveData<ServicesResponseWrapper<Data>> {
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    responseLiveData.value = ServicesResponseWrapper.Loading(
        null,
        "Loading..."
    )
    val request = authRequestInterface.approveReport(id, header)
    request.enqueue(object : Callback<DefaultResponse> {
        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
            onFailureResponse(responseLiveData, t)
        }

        override fun onResponse(
            call: Call<DefaultResponse>,
            response: Response<DefaultResponse>
        ) {
            onResponseTask(response as Response<Data>, responseLiveData)
        }

    })
    return responseLiveData
}




