package com.anapfoundation.covid_19volunteerapp.model.response

import com.anapfoundation.covid_19volunteerapp.model.AnotherError
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Reports (
    val data: List<ReportResponse>,
    message: String?,
    error: AnotherError?,
    token: String?
): Data(message, error, token), Serializable


class ReportResponse(
    var topic: String,
    var rating: String,
    var story: String,
    var state: String,
    @SerializedName("media_url")
    var mediaURL: String? = "",
    @SerializedName("local_government")
    var localGovernment: String? = "",
    var town: String? = "",
    @SerializedName("nearest_bus_stop")
    val nearestBusStop: String? = "",
    var approved: Boolean = false,
    @SerializedName("approved_by")
    val approvedBy: String? = "",

    @SerializedName("reported_by")
    val reportedBy: String? = "",

    @SerializedName("created_at")
    val createdAt: String? = "",

    @SerializedName("updated_at")
    val updatedAt: String? = "",

    @SerializedName("deleted_at")
    val deletedAt: String? = "",
    val index: Long? = 0

) : Serializable