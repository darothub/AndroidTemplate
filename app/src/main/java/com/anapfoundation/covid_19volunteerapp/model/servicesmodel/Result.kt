package com.anapfoundation.covid_19volunteerapp.model.servicesmodel

import java.io.Serializable

class Result (
    val message: String,
    val error: String?="",
    val token: String
): Serializable
