package com.anapfoundation.covid_19volunteerapp.model.servicesmodel

import com.anapfoundation.covid_19volunteerapp.model.Data
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ServiceResult (
    message: String?,
    error: AnotherError?,
    token: String?
): Data(message, error, token), Serializable

class AnotherError()

//open class MotorVehicle(val maxSpeed: Double, val horsepowers: Int)
//class Car(
//    val seatCount: Int,
//    maxSpeed: Double
//) : MotorVehicle(maxSpeed, 100)
//
//open class Hello()