package com.itzme.data.network

import com.eslam.mohamed.weatherapplication.data.models.weather.response.success.ResponseWeather
import com.eslam.mohamed.weatherapplication.utilits.Constants
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    @GET(Constants.WEATHER)
    fun GET_WEATHER(
        @Query("q") cityName: String,
        @Query("appid") key: String,
        @Query("units") units: String = "metric"
    )
            : Observable<ResponseWeather>
}