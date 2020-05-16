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
            responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
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
    houseNumber: String,
    state: String,
    street: String?,
    profileImageUrl: String?,
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
        profileImageUrl,
        header
    )
    request.enqueue(object : Callback<ProfileData> {
        override fun onFailure(call: Call<ProfileData>, t: Throwable) {
            responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
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
            responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
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

fun AuthViewModel.getUnApprovedReports(header: String, first:Long?, after:Long?): LiveData<ServicesResponseWrapper<Data>>{
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    responseLiveData.value = ServicesResponseWrapper.Loading(
        null,
        "Loading..."
    )
    val request = authRequestInterface.getUnapprovedReports(header, first, after)
    request.enqueue(object : Callback<Reports> {
        override fun onFailure(call: Call<Reports>, t: Throwable) {
            responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
        }

        override fun onResponse(
            call: Call<Reports>,
            response: Response<Reports>
        ) {
            onResponseTask(response as Response<Data>, responseLiveData)
        }

    })
    return responseLiveData
}



