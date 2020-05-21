package com.anapfoundation.covid_19volunteerapp.model.user

import com.anapfoundation.covid_19volunteerapp.model.AnotherError
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import java.io.Serializable

class UserResponse (
    val message: String?,
    val error: AnotherError?,
    val data:String?
): Data, Serializable


