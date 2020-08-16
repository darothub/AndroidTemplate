package com.anapfoundation.volunteerapp.model

import com.anapfoundation.volunteerapp.model.response.Data
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StatesList (
    val data: List<State>,
    val message:String?,
    val error: AnotherError?
): Data, Serializable
class State (
    val id: String,
    val index: Long,
    val state: String,
    val country: String,
    val zone:String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null
):Serializable


class LGA (
    val data: List<LocalGovernment>,
    val message:String?,
    val error: AnotherError?
): Data, Serializable


data class LocalGovernment (
    val id: String,
    val index: Long,
    val state: String?,
    val district: String?,

    @SerializedName("local_government")
    val localGovernment: String,

    val country: String?,
    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null

):Serializable

class Location(
    val data: LocationData,
    val message:String?,
    val error: AnotherError?
): Data, Serializable

data class LocationData(
    val id: String,
    val index: Long,
    val state: String,
    val district: String,

    @SerializedName("local_government")
    val localGovernment: String?,

    @SerializedName("state_name")
    val stateName:String?,

    @SerializedName("district_name")
    val districtName:String?,

    @SerializedName("district_count")
    val districtCount:Long?,
    @SerializedName("lg_count")
    val lgCount:Long?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null

)