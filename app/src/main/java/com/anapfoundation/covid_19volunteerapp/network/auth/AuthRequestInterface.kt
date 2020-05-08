package com.anapfoundation.covid_19volunteerapp.network.auth

import com.anapfoundation.covid_19volunteerapp.model.Report
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.TopicData
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call
import retrofit2.Callback

interface AuthRequestInterface {

    fun addReport(topic:String,
                  rating:String,
                  story:String,
                  state:String,
                  header:String): Call<ServiceResult>

    fun getTopic(header: String): Call<TopicData>

    fun getRating(topicID:String, header: String): Call<TopicData>

    fun getStates(header: String): Call<StatesList>
}