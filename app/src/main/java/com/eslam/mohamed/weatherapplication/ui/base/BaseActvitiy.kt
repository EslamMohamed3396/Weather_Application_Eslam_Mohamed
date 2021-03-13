package com.eslam.mohamed.weatherapplication.ui.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActvitiy<B : ViewDataBinding> : AppCompatActivity() {
    protected var binding: B? = null

    protected open fun bindView(
        @LayoutRes layoutRes: Int
    ) {
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding?.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}