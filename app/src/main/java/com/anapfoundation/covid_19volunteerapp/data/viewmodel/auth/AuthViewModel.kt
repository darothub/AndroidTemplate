package com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.request.AddReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class AuthViewModel @Inject constructor (val authRequestInterface: AuthRequestInterface, val retrofit: Retrofit,
                                         val paging:DataSource.Factory<Long, ReportResponse>):
    ViewModel() {

    val title:String by lazy{
        this.getName()

    }

    fun addReport(topic: String, rating:String, story:String, state:String, mediaURL:String?,
                  localGovernment:String?, district:String?,
                  town:String?, header:String): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.addReport(topic, rating, story, state, mediaURL, localGovernment, district, town, header)
        request.enqueue(object:Callback<AddReportResponse>{
            override fun onFailure(call: Call<AddReportResponse>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<AddReportResponse>, response: Response<AddReportResponse>) {
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

        request.enqueue(object :Callback<TopicResponse>{
            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
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
        request.enqueue(object:Callback<TopicResponse>{
            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
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

    fun getReports(header: String): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getReports(header)
        request.enqueue(object : Callback<Reports>{
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

    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        houseNumber: String,
        state: String,
        street: String,
        header: String
    ): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.updateProfile(firstName, lastName, email, phone, houseNumber, state, street, header)
        request.enqueue(object : Callback<ProfileData>{
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

    private fun onResponseTask(response: Response<Data>, responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>){
        val res = response.body()
        val statusCode = response.code()
        Log.i(title, "${response.code()}")

        when(statusCode) {

            401 -> {
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation{}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a))
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    responseLiveData.postValue(ServicesResponseWrapper.Logout(error?.message.toString()))
                }
                catch (e:Exception){
                    Log.i(title, e.message)
                }

            }
            in 400..500 ->{
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation{}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a))
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
                }
                catch (e:java.lang.Exception){
                    Log.i(title, e.message)
                }


            }
            else -> {
                try {
                    Log.i(title, "errors ${response.errorBody()}")
                    responseLiveData.postValue(ServicesResponseWrapper.Success(res))
                }catch (e:java.lang.Exception){
                    Log.i(title, e.message)
                }


            }
        }

    }



    private fun configPaged(size: Int): PagedList.Config = PagedList.Config.Builder()
        .setPageSize(size)
        .setInitialLoadSizeHint(size * 2)
        .setEnablePlaceholders(true)
        .build()

    fun getReportss():LiveData<PagedList<ReportResponse>>{
        return LivePagedListBuilder(paging, configPaged(3)).build()
    }
}