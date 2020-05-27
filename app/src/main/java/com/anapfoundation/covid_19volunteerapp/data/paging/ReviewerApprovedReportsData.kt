package com.anapfoundation.covid_19volunteerapp.data.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import com.utsman.recycling.paged.extentions.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ReviewerApprovedReportsDataFactory @Inject constructor(val authApiRequests: AuthApiRequests, val storageRequest: StorageRequest) : DataSource.Factory<Long, ReportResponse>() {
    val pagingLiveData = MutableLiveData<ReviewerApprovedReportsDataSource>()
    override fun create(): DataSource<Long, ReportResponse> {
        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"
        Log.i("userToken", "$header")
        val data = ReviewerApprovedReportsDataSource(authApiRequests, header)
        pagingLiveData.postValue(data)
        return data
    }
}

class ReviewerApprovedReportsDataSource(val authApiRequests: AuthApiRequests, val header:String):
    ItemKeyedDataSource<Long, ReportResponse>() {

    var networkState = MutableLiveData<NetworkState>()
    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<ReportResponse>
    ) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getApprovedReports(header, params.requestedLoadSize.toLong())
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))

            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                Log.i("title", "Approvedbody $body")
                when {
                    body != null -> {
                        networkState.postValue(NetworkState.LOADED)
                        callback.onResult(body.data)
                    }

                }
            }

        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {
        val request = authApiRequests.getApprovedReportsAfter(
            header,
            params.requestedLoadSize.toLong(),
            params.key
        )
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null -> {
                        try {

                            networkState.postValue(NetworkState.LOADED)
                            callback.onResult(body.data)
                        } catch (e: Exception) {
                            Log.e("Paging error", e.localizedMessage)
                        }

                    }
                }
            }

        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {}

    override fun getKey(item: ReportResponse): Long = item.index!!
}

