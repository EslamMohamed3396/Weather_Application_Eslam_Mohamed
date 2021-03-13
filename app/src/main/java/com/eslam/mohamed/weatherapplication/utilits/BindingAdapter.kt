package com.eslam.mohamed.weatherapplication.utilits

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.eslam.mohamed.weatherapplication.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:icon")
fun imageUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrBlank()) {
        Glide.with(imageView.context)
            .load(Constants.URL_IMAGE + url + Constants.MIME_TYPE)
            .placeholder(R.drawable.app_icon)
            .into(imageView)
    }

}

@BindingAdapter("app:update")
fun update(textView: TextView, update: Long) {
    textView.text = "Updated at: " +
            SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.getDefault()
            ).format(Date(update * 1000))

}

@BindingAdapter("app:sunrise")
fun sunrise(textView: TextView, sunrise: Long) {
    textView.text = "Sunrise : " +
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(sunrise * 1000))

}

@BindingAdapter("app:sunset")
fun sunset(textView: TextView, sunset: Long) {
    textView.text = "Sunset : " +
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(sunset * 1000))

}

@BindingAdapter("app:temp")
fun temp(textView: TextView, temp: Double) {
    textView.text = "${temp.toInt()}℃"

}

@BindingAdapter("app:tempMin","app:tempMax")
fun tempMinMax(textView: TextView, tempMin: Double, tempMax: Double) {
    textView.text = "${tempMin.toInt()} ℃ / ${tempMax.toInt()} ℃ "

}

