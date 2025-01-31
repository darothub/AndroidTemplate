package com.anapfoundation.volunteerapp.data.viewmodel.user

import androidx.lifecycle.MutableLiveData
import com.anapfoundation.volunteerapp.model.Location
import com.anapfoundation.volunteerapp.model.response.Data
import com.anapfoundation.volunteerapp.services.ServicesResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun UserViewModel.getSingleLGA(lgaID:String): MutableLiveData<ServicesResponseWrapper<Data>> {
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    responseLiveData.value = ServicesResponseWrapper.Loading(
        null,
        "Loading..."
    )
    val request = userRequestInterface.getSingleLGA(lgaID)
    request.enqueue(object : Callback<Location> {
        override fun onFailure(call: Call<Location>, t: Throwable) {
            onFailureResponse(responseLiveData, t)
        }

        override fun onResponse(call: Call<Location>, response: Response<Location>) {
            onResponseTask(response as Response<Data>, responseLiveData)
        }

    })
    return responseLiveData
}