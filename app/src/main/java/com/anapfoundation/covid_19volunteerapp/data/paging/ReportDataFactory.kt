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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class ReportDataFactory @Inject constructor(val authApiRequests: AuthApiRequests, val storageRequest: StorageRequest) : DataSource.Factory<Long, ReportResponse>() {
    val pagingLiveData = MutableLiveData<ReportsDataSource>()
    override fun create(): DataSource<Long, ReportResponse> {
        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"
        val data = ReportsDataSource(authApiRequests, header)
        pagingLiveData.postValue(data)
        return data
    }
}

class ReportsDataSource(val authApiRequests: AuthApiRequests, val header:String):ItemKeyedDataSource<Long, ReportResponse>(){
    private var first = 10L
    private var after = 0L
    val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<ReportResponse>
    ) {
        val request = authApiRequests.getReportss(header, first, after)
        request.enqueue(object : Callback<Reports>{
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                responseLiveData.postValue(ServicesResponseWrapper.Logout(t.message.toString()))
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        after++
                        responseLiveData.postValue(ServicesResponseWrapper.Success(body))
                        callback.onResult(body.data)
                    }
                }
            }

        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<ReportResponse>) {
        val request = authApiRequests.getReportss(header, 1, 0)
        request.enqueue(object : Callback<Reports>{
            override fun onFailure(call: Call<Reports>, t: Throwable) {
                Log.i("Datasource", "error message ${t.message}")
                responseLiveData.postValue(ServicesResponseWrapper.Error(t.message.toString()))
            }

            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
                val body = response.body()
                when {

                    body != null ->  {
                        try {
                            after++
                            responseLiveData.value = ServicesResponseWrapper.Loading(
                                null,
                                "Loading..."
                            )
                            responseLiveData.postValue(ServicesResponseWrapper.Success(body))
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