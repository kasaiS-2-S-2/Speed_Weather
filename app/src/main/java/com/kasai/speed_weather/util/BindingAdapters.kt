package com.kasai.speed_weather.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kasai.speed_weather.model.WeatherInfo

@BindingAdapter("hourlyTemps", "position", requireAll = true)
fun showHourlyTemp(view: TextView, hourlyTemps: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyTemp = hourlyTemps?.get(position)?.temp
    var tempsWithC: Long = 0
    var hourlyTempString = ""
    if (hourlyTemp != null) {
        hourlyTempString = " " + Math.round(hourlyTemp) + " "
    }

    view.setText(hourlyTempString)
}
