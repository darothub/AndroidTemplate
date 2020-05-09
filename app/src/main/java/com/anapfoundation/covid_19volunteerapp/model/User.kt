package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.AnotherError
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
    var rememberPassword:Boolean = false


}
class ProfileData (
    val data: UserData,
    message: String?,
    error: AnotherError?,
    token: String?
):Serializable, Data(message, error, token)

class UserData(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val age: Long? = 0,
    val email: String,
    val phone: String,

    @SerializedName("instagram_profile_username")
    val instagramProfileUsername: String? = "",

    @SerializedName("twitter_profile_username")
    val twitterProfileUsername: String? = "",

    val specialization: String? = "",

    @SerializedName("profile_image_url")
    val profileImageURL: String? = "",

    @SerializedName("house_number")
    val houseNumber: String? = "",

    val street: String? = "",
    val id: String,
    val state: String? = "",
    @SerializedName("state_id")
    val stateID: String? = "",
    @SerializedName("total_reports")
    val totalReports: Long
):Serializable