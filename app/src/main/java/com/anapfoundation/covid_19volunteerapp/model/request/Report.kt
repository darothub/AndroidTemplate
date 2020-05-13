package com.anapfoundation.covid_19volunteerapp.model.request

import java.io.Serializable

class Report(
    var topic: String,
    var rating: String,
    var story: String,
    var state: String,
    var mediaURL: String? = "",
    var localGovernment: String? = "",
    var district:String? = "",
    var town: String?=""
) : Serializable
