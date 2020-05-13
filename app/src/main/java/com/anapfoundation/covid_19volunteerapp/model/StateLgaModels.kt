package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StatesList (
    val data: List<State>,
    message:String?,
    error: AnotherError?,
    token:String?
): Data(message, error, token), Serializable
class State (
    val id: String,
    val index: Long,
    val state: String,
    val country: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null
):Serializable


class LGA (
    val data: List<LocalGovernment>,
    message:String?,
    error: AnotherError?,
    token:String?
): Data(message, error, token), Serializable


data class LocalGovernment (
    val id: String,
    val index: Long,
    val state: String,
    val district: String,

    @SerializedName("local_government")
    val localGovernment: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null
):Serializable