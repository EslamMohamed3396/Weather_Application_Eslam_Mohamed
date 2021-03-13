package com.eslam.mohamed.weatherapplication.ui.actvity

import androidx.lifecycle.LiveData
import com.eslam.mohamed.weatherapplication.data.models.weather.response.success.ResponseWeather
import com.eslam.mohamed.weatherapplication.ui.base.BaseViewModel
import com.eslam.mohamed.weatherapplication.utilits.Resource
import com.itzme.data.network.Client

class MainActvityViewModel : BaseViewModel<ResponseWeather>() {

    fun getWeather(cityName: String, key: String): LiveData<Resource<ResponseWeather>> {
        return callApi(Client.getInstance()?.GET_WEATHER(cityName, key)!!)
    }
}