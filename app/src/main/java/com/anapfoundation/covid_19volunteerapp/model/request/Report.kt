package com.anapfoundation.covid_19volunteerapp.model.request

import com.anapfoundation.covid_19volunteerapp.model.AnotherError
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import java.io.Serializable
class AddReportResponse (
    val data: ReportResponse,
    val message: String?,
    val error: AnotherError?
): Data, Serializable

class Report(
    var topic: String,
    var rating: String,
    var story: String,
    var state: String,
    var mediaURL: String? = null,
    var localGovernment: String? = "",
    var district:String? = "",
    var town: String?="",
    var suggestion: String?=null
) : Serializable
