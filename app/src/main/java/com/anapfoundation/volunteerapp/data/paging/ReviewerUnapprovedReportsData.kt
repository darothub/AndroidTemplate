package com.anapfoundation.volunteerapp.data.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.model.response.Reports
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.volunteerapp.services.authservices.AuthApiRequests
import com.utsman.recycling.paged.extentions.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class ReviewerUnapprovedReportsDataFactory @Inject constructor(val authApiRequests: AuthApiRequests, val storageRequest: StorageRequest) : DataSource.Factory<Long, ReportResponse>() {
    val pagingLiveData = MutableLiveData<ReviewerUnapprovedReportsDataSource>()
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<String>>()
    override fun create(): DataSource<Long, ReportResponse> {
        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"
        val data = ReviewerUnapprovedReportsDataSource(authApiRequests, header)
        pagingLiveData.postValue(data)
        if(user?.token.isNullOrEmpty()){
            responseLiveData.postValue(ServicesResponseWrapper.Logout("Unauthorized", 401))
        }

        return data
    }
}

class ReviewerUnapprovedReportsDataSource(val authApiRequests: AuthApiRequests, val header:String):
    ItemKeyedDataSource<Long, ReportResponse>(){
    var networkState = MutableLiveData<NetworkState>()
    var countLiveData = MutableLiveData<Int>()
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
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                Log.i("title", "UnApprovedbody $body")
                when {
                    body != null ->  {
                        networkState.postValue(NetworkState.LOADED)
                        countLiveData.postValue(params.requestedLoadSize)
                        callback.onResult(body.data)
                    }

                }
            }

        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getUnapprovedReportsAfter(header, params.requestedLoadSize.toLong()+1, params.key)
        request.enqueue(object : Callback<Reports> {
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))

            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        try {

                            networkState.postValue(NetworkState.LOADED)
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