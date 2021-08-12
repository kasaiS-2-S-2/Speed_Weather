package com.kasai.speed_weather.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kasai.speed_weather.model.WeatherInfo

@BindingAdapter("app:showAllHourlyTemp")
fun showAllHourlyTemp(view: TextView, hourlyWeatherTemps: List<WeatherInfo.Hourly>?) {

    var hourlyTemps = ""
    if (hourlyWeatherTemps != null) {
        for (item in hourlyWeatherTemps) {
            val temp = Math.round(item.temp - 273.15)
            hourlyTemps = hourlyTemps + "\n" + temp
        }
    }
    view.setText(hourlyTemps)
}

@BindingAdapter("hourlyTemps", "position", requireAll = true)
fun showHourlyTemp(view: TextView, hourlyTemps: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyTemp = hourlyTemps?.get(position)?.temp
    var tempsWithC: Long = 0
    if (hourlyTemp != null) {
        tempsWithC = Math.round(hourlyTemp - 273.15)
    }

    val hourlyTempString = " " + tempsWithC + " "

    view.setText(hourlyTempString)
}
