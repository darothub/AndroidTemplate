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


class ReviewerUnapprovedReportsDataFactory @Inject constructor(val authApiRequests: AuthApiRequests, val storageRequest: StorageRequest) : DataSource.Factory<Long, ReportResponse>() {
    val pagingLiveData = MutableLiveData<ReviewerUnapprovedReportsDataSource>()
    val pagingCount = MutableLiveData<Int?>()
    override fun create(): DataSource<Long, ReportResponse> {
        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"
        val data = ReviewerUnapprovedReportsDataSource(authApiRequests, header)
        pagingLiveData.postValue(data)
        pagingCount.postValue(data.count)
        return data
    }
}

class ReviewerUnapprovedReportsDataSource(val authApiRequests: AuthApiRequests, val header:String):
    ItemKeyedDataSource<Long, ReportResponse>(){
    private var first = 10L
    private var after = 0L
    var count :Int = 0
    var networkState = MutableLiveData<NetworkState>()
    var countLiveData = MutableLiveData<Int?>()
    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<ReportResponse>
    ) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getUnapprovedReports(header, params.requestedLoadSize.toLong())
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))
                countLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        networkState.postValue(NetworkState.LOADED)
                        count = params.requestedLoadSize
                        countLiveData.postValue(params.requestedLoadSize)
                        Log.i("last count", "$countLiveData")
                        callback.onResult(body.data)
                    }
                }
            }

        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getUnapprovedReportsAfter(header, params.requestedLoadSize.toLong(), params.key)
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))
                countLiveData.postValue(null)
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        try {
                            count += params.requestedLoadSize
                            networkState.postValue(NetworkState.LOADING)
                            countLiveData.postValue(params.requestedLoadSize)
                            callback.onResult(body.data)
                        }
                        catch (e:Exception){
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