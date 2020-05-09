package com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class AuthViewModel @Inject constructor (val authRequestInterface: AuthRequestInterface, val retrofit: Retrofit):
    ViewModel() {

    val title:String by lazy{
        this.getName()

    }

    fun addReport(topic: String, rating:String, story:String, state:String, header:String): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.addReport(topic, rating, story, state, header)
        request.enqueue(object:Callback<ServiceResult>{
            override fun onFailure(call: Call<ServiceResult>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<ServiceResult>, response: Response<ServiceResult>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }
    fun getTopic(header: String): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getTopic(header)

        request.enqueue(object :Callback<TopicData>{
            override fun onFailure(call: Call<TopicData>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<TopicData>, response: Response<TopicData>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun getRating(topicID:String, header:String): LiveData<ServicesResponseWrapper<Data>>{

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getRating(topicID, header)
        request.enqueue(object:Callback<TopicData>{
            override fun onFailure(call: Call<TopicData>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<TopicData>, response: Response<TopicData>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun getStates(header: String): LiveData<ServicesResponseWrapper<Data>>{

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getStates(header)

        request.enqueue(object :Callback<StatesList>{
            override fun onFailure(call: Call<StatesList>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<StatesList>, response: Response<StatesList>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData

    }

    fun getProfileData(header: String): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getProfileData(header)

        request.enqueue(object :Callback<ProfileData>{
            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    private fun onResponseTask(response: Response<Data>, responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>){
        val res = response.body()
        Log.i(title, "${response.code()}")
        when(response.code()) {
            in 400..500 ->{
                Log.i(title, "errorbody ${response.raw()}")
                val a = object : Annotation{}
                val converter = retrofit.responseBodyConverter<ServiceResult>(ServiceResult::class.java, arrayOf(a))
                val error = converter.convert(response.errorBody())
                Log.i(title, "message ${error?.message}")
                responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
            }
            else -> {
                Log.i(title, "errors ${response.errorBody()}")
                responseLiveData.postValue(ServicesResponseWrapper.Success(res))
            }
        }

    }

}