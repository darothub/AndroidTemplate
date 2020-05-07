package com.anapfoundation.covid_19volunteerapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class User(
    @SerializedName("firstName")
    var firstName: String?,
    @SerializedName("lastName")
    var lastName: String?,
    @SerializedName("email")
    var email:String?,
    @SerializedName("password")
    var password:String?="",
    @SerializedName("phone")
    var phone:String?

) : Serializable {

    var loggedIn:Boolean = false
    var message = ""
    var token:String?=""


}