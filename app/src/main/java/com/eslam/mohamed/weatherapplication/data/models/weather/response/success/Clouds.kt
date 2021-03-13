package com.eslam.mohamed.weatherapplication.data.models.weather.response.success


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Clouds(
    @SerializedName("all")
    val all: Int?
) : Parcelable