package com.anapfoundation.volunteerapp.data.viewmodel.auth

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.anapfoundation.volunteerapp.data.paging.*
import com.anapfoundation.volunteerapp.model.*
import com.anapfoundation.volunteerapp.model.response.Data
import com.anapfoundation.volunteerapp.model.response.TopicResponse
import com.anapfoundation.volunteerapp.model.DefaultResponse
import com.anapfoundation.volunteerapp.model.request.AddReportResponse
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.model.response.Reports
import com.anapfoundation.volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.volunteerapp.utils.extensions.getName
import com.utsman.recycling.paged.extentions.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRequestInterface: AuthRequestInterface, val retrofit: Retrofit

) :
    ViewModel() {

    val title: String by lazy {
        this.getName()

    }

    var networkState = MutableLiveData<com.utsman.recycling.extentions.NetworkState>()
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<String>>()


    fun addReport(
        topic: String, rating: String, story: String, state: String, mediaURL: String?="",
        localGovernment: String?, district: String?,
        town: String?, zone:String?, suggestion:String?, header: String
    ): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.addReport(
            topic,
            rating,
            story,
            state,
            mediaURL,
            localGovernment,
            district,
            town,
            zone,
            suggestion,
            header
        )
        request.enqueue(object : Callback<AddReportResponse> {
            override fun onFailure(call: Call<AddReportResponse>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(
                call: Call<AddReportResponse>,
                response: Response<AddReportResponse>
            ) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun getTopic(header: String): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        networkState.postValue(com.utsman.recycling.extentions.NetworkState.LOADING)
        val request = authRequestInterface.getTopic(header)

        request.enqueue(object : Callback<TopicResponse> {
            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                networkState.postValue(com.utsman.recycling.extentions.NetworkState.error("Bad network connnection"))
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.cause}", null))
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun getRating(topicID: String, header: String): LiveData<ServicesResponseWrapper<Data>> {

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getRating(topicID, header)
        request.enqueue(object : Callback<TopicResponse> {
            override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }




    fun getProfileData(header: String): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getProfileData(header)

        request.enqueue(object : Callback<ProfileData> {
            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun getReports(header: String, first:Long?, after:Long?): LiveData<ServicesResponseWrapper<Data>> {
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = authRequestInterface.getReports(header, first, after)
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
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


    internal fun onFailureResponse(
        responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>,
        t: Throwable
    ) {
        responseLiveData.postValue(ServicesResponseWrapper.Error(t.localizedMessage, 0, null))
    }

    internal fun onResponseTask(
        response: Response<Data>,
        responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>
    ) {
        val res = response.body()
        val statusCode = response.code()
        Log.i(title, "${response.code()}")

        when (statusCode) {

            401 -> {
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation {}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a)
                    )
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    responseLiveData.postValue(ServicesResponseWrapper.Logout("${response.message()} ${error?.message.toString()}"))
                } catch (e: Exception) {
                    Log.i(title, e.message)
                }

            }
            in 400..500 -> {
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation {}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a)
                    )
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    networkState.postValue(com.utsman.recycling.extentions.NetworkState.error(error?.message))
                    responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
                } catch (e: java.lang.Exception) {
                    Log.i(title, "Caught ${e.message}")
                }

            }
            else -> {
                try {
                    Log.i(title, "token ${res}")
                    networkState.postValue(com.utsman.recycling.extentions.NetworkState.LOADED)
                    responseLiveData.postValue(ServicesResponseWrapper.Success(res))
                } catch (e: java.lang.Exception) {
                    Log.i(title, e.message)
                }


            }
        }

    }

    fun getAuthLoader():LiveData<com.utsman.recycling.extentions.NetworkState> = networkState

    private fun configPaged(size: Int): PagedList.Config = PagedList.Config.Builder()
        .setPageSize(size)
        .setInitialLoadSizeHint(size)
        .setEnablePlaceholders(true)
        .build()

    fun getReportss(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<PagedList<ReportResponse>> {

        return LivePagedListBuilder(dataSourceFactory, configPaged(1)).build()
    }
    fun getUnapprovedReports(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<PagedList<ReportResponse>>{
        return LivePagedListBuilder(dataSourceFactory, configPaged(600)).build()
    }
    fun getApprovedReports(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<PagedList<ReportResponse>>{
        return LivePagedListBuilder(dataSourceFactory, configPaged(600)).build()
    }

    fun getReporterLoader(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<NetworkState> = Transformations.switchMap<ReportsDataSource, NetworkState>(
            (dataSourceFactory as ReportDataFactory).pagingLiveData
    ) {
        it.networkState
    }

    fun approvedLoader(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<NetworkState> = Transformations.switchMap(
        (dataSourceFactory as ReviewerApprovedReportsDataFactory).pagingLiveData
    ) {
        it.networkState
    }

    fun unApprovedReportLoader(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<NetworkState> = Transformations.switchMap(
        (dataSourceFactory as ReviewerUnapprovedReportsDataFactory).pagingLiveData
    ) {
        it.networkState
    }

    fun getReporterCount(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<Int> = Transformations.switchMap(
        (dataSourceFactory as ReportDataFactory).pagingLiveData
    ) {
        it.countLiveData
    }

    fun getUnapprovedReportCount(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<Int> = Transformations.switchMap(
        (dataSourceFactory as ReviewerUnapprovedReportsDataFactory).pagingLiveData
    ) {
        it.countLiveData
    }

    fun getApprovedReportCount(dataSourceFactory: DataSource.Factory<Long, ReportResponse>): LiveData<Int> = Transformations.switchMap(
        (dataSourceFactory as ReviewerApprovedReportsDataFactory).pagingLiveData
    ) {
        it.countLiveData
    }



}