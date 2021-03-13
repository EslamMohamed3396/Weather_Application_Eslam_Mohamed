package com.eslam.mohamed.weatherapplication.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eslam.mohamed.weatherapplication.utilits.Resource
import com.itzme.data.network.Client
import com.itzme.data.network.ICallBackNetwork
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

open class BaseViewModel<T> : ViewModel() {
    private var disposable: Disposable? = null
    private var responseMutableLiveData = MutableLiveData<Resource<T>>()

    open fun callApi(api: Observable<T>): LiveData<Resource<T>> {
        responseMutableLiveData.value = Resource.Loading()

        Client.getInstance()?.request(api, object : ICallBackNetwork<T> {
            override fun onSuccess(response: T?) {
                onResponse(response!!)
            }

            override fun onDisposable(d: Disposable?) {
                disposable = d
            }

            override fun onFailed(error: String?, code: Int) {
                onFailure(error!!, code)
            }

        })
        return responseMutableLiveData
    }


    private fun onFailure(t: String, code: Int) {
        responseMutableLiveData.value = Resource.Error(t, code)

    }

    private fun onResponse(response: T) {
        responseMutableLiveData.value = Resource.Success(response)
    }

    override fun onCleared() {
        super.onCleared()
        if (disposable != null) {
            disposable!!.dispose()
        }
    }
}