package com.eslam.mohamed.weatherapplication.data.models.weather.response.error

import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("cod")
    val cod: Int? = null

    @SerializedName("message")
    val message: String? = null
}