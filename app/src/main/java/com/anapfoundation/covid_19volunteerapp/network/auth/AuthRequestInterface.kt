package com.anapfoundation.covid_19volunteerapp.network.auth

import com.anapfoundation.covid_19volunteerapp.model.Report
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call

interface AuthRequestInterface {

    fun addReport(topic:String,
                  rating:String,
                  story:String,
                  state:String,
                  header:String): Call<ServiceResult>
}