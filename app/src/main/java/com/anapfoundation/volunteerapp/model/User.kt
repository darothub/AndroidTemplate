package com.anapfoundation.volunteerapp.model

import com.anapfoundation.volunteerapp.model.response.Data
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
    var imageUrl = ""
    var street: String? = null
    var lgName:String?=null
    var stateName:String?=null
    var lgID:String?=null
    var stateID:String?=null
    var zoneID:String?=null
    var districtID:String?=null
    var isReviewer:Boolean = false
    var houseNumber: String? = null
    val instagramProfileUsername: String? = null
    val twitterProfileUsername: String? = null
    val specialization: String? = null
    var totalReports: Long?=0
    var totalUnapprovedReports: Long?=0
    var totalApprovedReports:Long =0
    var id:String?=null


}
class ProfileData (
    val data: UserData,
    val message: String?,
    val error: AnotherError?
):Serializable, Data

class UserData(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    val phone: String,

    @SerializedName("password")
    var password:String="",

    val age: Long? = null,
    @SerializedName("house_number")
    val houseNumber: String? = null,

    val street: String? = null,
    val id: String? = null,
    @SerializedName("state")
    val stateID: String? = null,
    @SerializedName("state_name")
    val stateName:String?=null,
    @SerializedName("lg_name")
    val lgName:String?=null,
    @SerializedName("local_government")
    val lgID:String?=null,
    @SerializedName("zone")
    val zoneID:String?=null,
    @SerializedName("district")
    val districtID:String?=null,

    @SerializedName("instagram_profile_username")
    val instagramProfileUsername: String? = null,

    @SerializedName("twitter_profile_username")
    val twitterProfileUsername: String? = null,

    val specialization: String? = null,

    val isReviewer:Boolean?=false,

    @SerializedName("profile_image_url")
    val profileImageURL: String? = null,

    @SerializedName("total_reports")
    val totalReports: Long?=null
):Serializable {

}