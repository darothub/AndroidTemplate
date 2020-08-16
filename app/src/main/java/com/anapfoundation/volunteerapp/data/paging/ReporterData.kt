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


class ReportDataFactory @Inject constructor(val authApiRequests: AuthApiRequests, val storageRequest: StorageRequest) : DataSource.Factory<Long, ReportResponse>() {
    val pagingLiveData = MutableLiveData<ReportsDataSource>()
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<String>>()
    override fun create(): DataSource<Long, ReportResponse> {
        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"
        val data = ReportsDataSource(authApiRequests, header)
        pagingLiveData.postValue(data)
        if(user?.token.isNullOrEmpty()){
            responseLiveData.postValue(ServicesResponseWrapper.Logout("Unauthorized", 401))
        }

        return data
    }
}

class ReportsDataSource(val authApiRequests: AuthApiRequests, val header:String):ItemKeyedDataSource<Long, ReportResponse>(){


    var networkState = MutableLiveData<NetworkState>()
    val countLiveData = MutableLiveData<Int>()
    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<ReportResponse>
    ) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getReport(header, params.requestedLoadSize.toLong())
        request.enqueue(object : Callback<Reports>{
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error("Bad network connection"))

            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        countLiveData.postValue(params.requestedLoadSize)
                        networkState.postValue(NetworkState.LOADED)

                        callback.onResult(body.data)
                    }
                }
            }

        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {
        networkState.postValue(NetworkState.LOADING)
        val request = authApiRequests.getReportAfter(header, params.requestedLoadSize.toLong(), params.key)
        request.enqueue(object : Callback<Reports>{
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                networkState.postValue(NetworkState.error(t.localizedMessage))

//                countLiveData.postValue(ServicesResponseWrapper.Error(t.message.toString()))
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        try {
                            countLiveData.postValue(params.requestedLoadSize)
                            networkState.postValue(NetworkState.LOADED)

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