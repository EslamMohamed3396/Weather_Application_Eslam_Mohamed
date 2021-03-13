package com.eslam.mohamed.weatherapplication.utilits

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.eslam.mohamed.weatherapplication.R

object DialogUtil {
    private var dialog: Dialog? = null

    fun showDialog(context: Context?) {
        if (dialog != null && dialog?.isShowing!!) {
            return
        }
        dialog = Dialog(context!!)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(R.layout.dialog_loading)
        dialog?.setCancelable(false)
        dialog?.show()
    }

    fun dismissDialog() {
        if (dialog != null && dialog?.isShowing!!) {
            dialog?.dismiss()
        }
        dialog = null
    }

}