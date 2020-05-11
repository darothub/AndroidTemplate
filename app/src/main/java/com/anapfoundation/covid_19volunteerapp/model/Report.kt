package com.anapfoundation.covid_19volunteerapp.model

import java.io.Serializable

class Report(
    var topic: String,
    var rating: String,
    var story: String,
    var state: String,
    var mediaURL: String? = "",
    var localGovernment: String? = "",
    var town: String? = ""
) : Serializable
