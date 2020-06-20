package com.anapfoundation.volunteerapp.model.user

import com.anapfoundation.volunteerapp.model.AnotherError
import com.anapfoundation.volunteerapp.model.response.Data
import java.io.Serializable

class UserResponse (
    val message: String?,
    val error: AnotherError?,
    val data:String?
): Data, Serializable


