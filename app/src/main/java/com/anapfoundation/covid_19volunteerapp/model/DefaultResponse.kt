package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.response.Data
import java.io.Serializable

class DefaultResponse (
    val message: String?,
    val error: AnotherError?,
    val data:AnotherData?
): Data, Serializable

class AnotherError()

class AnotherData()

