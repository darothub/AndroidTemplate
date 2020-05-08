package com.anapfoundation.covid_19volunteerapp.model

import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.AnotherError
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TopicData (
    val data: List<Topic>, 
    message: String?,
    error: AnotherError?,
    token: String?
): Data(message, error, token), Serializable

data class Topic (
    val id: String,
    val index: Long,
    val topic: String,
    val rating:String?=null,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null
):Serializable