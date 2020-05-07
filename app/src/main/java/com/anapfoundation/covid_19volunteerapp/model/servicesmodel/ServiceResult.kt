package com.anapfoundation.covid_19volunteerapp.model.servicesmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ServiceResult (
    @SerializedName("message")
    var message: String?,
    @SerializedName("error")
    var error: AnotherError?,
    @SerializedName("token")
    var token: String?
): Serializable

class AnotherError()