package com.itzme.data.network

import com.eslam.mohamed.weatherapplication.data.models.weather.response.error.ErrorResponse
import com.eslam.mohamed.weatherapplication.data.models.weather.response.success.ResponseWeather
import com.eslam.mohamed.weatherapplication.utilits.Constants
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object Client {

    private var sBuilder: OkHttpClient.Builder? = null
    private var apiService: Api? = null
    private var SINSTANCE: Client? = null
    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder? {
        return try {
            if (sBuilder == null) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                sBuilder = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)

            }
            sBuilder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(getUnsafeOkHttpClient()?.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        apiService = retrofit.create(Api::class.java)
    }

    fun getInstance(): Client? {
        if (SINSTANCE == null) {
            SINSTANCE = Client
        }
        return SINSTANCE
    }


    fun <T> request(api: Observable<T>, callBackNetwork: ICallBackNetwork<T>) {
        api.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {
                    callBackNetwork.onDisposable(d)
                }

                override fun onNext(u: T) {
                    callBackNetwork.onSuccess(u)
                }

                override fun onError(e: Throwable) {

                    if (e is HttpException) {
                        val errorBody: String = e.response()?.errorBody()?.string()!!
                        val errorResponse: ErrorResponse =
                            Gson().fromJson(errorBody, ErrorResponse::class.java)
                        callBackNetwork.onFailed(
                            errorResponse.message,
                            errorResponse.cod!!
                        )
                    }
                }

                override fun onComplete() {}
            })
    }

    fun GET_WEATHER(cityName: String, key: String): Observable<ResponseWeather> {
        return apiService?.GET_WEATHER(cityName, key)!!
    }

}
