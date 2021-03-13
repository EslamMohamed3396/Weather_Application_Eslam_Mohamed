package com.itzme.data.network

import io.reactivex.disposables.Disposable

interface ICallBackNetwork<U> {
    fun onSuccess(response: U?)

    fun onDisposable(d: Disposable?)

    fun onFailed(error: String?, code: Int)
}