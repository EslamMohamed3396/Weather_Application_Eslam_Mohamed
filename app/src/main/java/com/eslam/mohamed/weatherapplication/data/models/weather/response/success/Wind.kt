package com.eslam.mohamed.weatherapplication.data.models.weather.response.success


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Wind(
    @SerializedName("speed")
    val speed: Double?,
    @SerializedName("deg")
    val deg: Int?
) : Parcelable