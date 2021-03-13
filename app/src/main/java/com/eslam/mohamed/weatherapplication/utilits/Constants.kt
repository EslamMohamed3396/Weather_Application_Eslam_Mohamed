package com.eslam.mohamed.weatherapplication.utilits

object Constants {

    //region api
    const val BASE_URL = "https://api.openweathermap.org/"
    const val URL_IMAGE = "https://openweathermap.org/img/wn/"
    const val MIME_TYPE = "@2x.png"

    private const val ROUTE = "data/2.5/"
    const val WEATHER = ROUTE + "weather"


    //endregion


    //region permissions
    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002
    const val PERMISSIONS_REQUEST_ENABLE_GPS = 9003
    const val ERROR_DIALOG_REQUEST = 9004
    //endregion
}