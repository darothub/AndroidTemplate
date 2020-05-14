package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.response.Data
import java.io.Serializable

class DefaultResponse (
    message: String?,
    error: AnotherError?,
    token: String?,
    val data:String?
): Data(message, error, token), Serializable

class AnotherError()

