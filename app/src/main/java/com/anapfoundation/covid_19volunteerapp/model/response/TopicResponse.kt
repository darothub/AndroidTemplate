package com.anapfoundation.covid_19volunteerapp.model.response

import com.anapfoundation.covid_19volunteerapp.model.AnotherError
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TopicResponse (
    val data: List<Topic>,
    val message: String?,
    val error: AnotherError?
): Data, Serializable

data class Topic (
    val id: String,
    val index: Long,
    val topic: String,
    val rating:String?="",
    @SerializedName("topic_id")
    val topicID:String?="",

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("deleted_at")
    val deletedAt: Any? = null
):Serializable