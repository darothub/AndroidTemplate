package com.anapfoundation.covid_19volunteerapp.network

import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import retrofit2.Call

interface PagingInterface {

    fun getReports(header:String, perPage:Int, page:Int): Call<Reports>
}