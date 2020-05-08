package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.AnotherError
import com.google.gson.annotations.SerializedName

open class Data(
    @SerializedName("message")
    val message: String?,
    @SerializedName("error")
    val error: AnotherError?,
    @SerializedName("token")
    val token: String?
) {
}