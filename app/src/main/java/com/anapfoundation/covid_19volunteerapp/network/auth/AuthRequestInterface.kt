package com.anapfoundation.covid_19volunteerapp.network.auth

import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import retrofit2.Call
import retrofit2.Callback

interface AuthRequestInterface {

    fun addReport(topic:String,
                  rating:String,
                  story:String,
                  state:String,
                  mediaURL:String?,
                  localGovernment:String?,
                  town:String?,
                  header:String): Call<ServiceResult>

    fun getTopic(header: String): Call<TopicData>

    fun getRating(topicID:String, header: String): Call<TopicData>

    fun getStates(first: String, after:String?=""): Call<StatesList>

    fun getProfileData(header: String):Call<ProfileData>

    fun getLocal(stateID:String): Call<LGA>
}