package com.kasai.speed_weather.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.kasai.speed_weather.R
import com.kasai.speed_weather.model.WeatherInfo
import java.util.*

@BindingAdapter("hourly_weather_icon_id", "position", requireAll = true)
fun setHourlyWeatherIcon(view: ImageView, hourlyInfo: List<WeatherInfo.Hourly>?, position: Int) {
    val hourlyWeatherIconID = hourlyInfo?.get(position)?.weather?.get(0)?.icon

    if (hourlyWeatherIconID != null) {
        val hourlyWeatherIcon = getWeatherIcon(hourlyWeatherIconID, view.context)
        if (hourlyWeatherIcon != null) {
            view.setImageDrawable(hourlyWeatherIcon)
        }
    }
}

@BindingAdapter("hourly_temps", "position", requireAll = true)
fun showHourlyTemp(view: TextView, hourlyInfo: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyTemp = hourlyInfo?.get(position)?.temp
    var hourlyTempString = ""
    if (hourlyTemp != null) {
        hourlyTempString = " " + Math.round(hourlyTemp) + " "
    }

    view.setText(hourlyTempString)
}

@BindingAdapter("hourly_weathers", "position", requireAll = true)
fun showHourlyWeather(view: TextView, hourlyInfo: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyWeather = hourlyInfo?.get(position)?.weather?.get(0)?.description
    var hourlyWeatherString = ""
    if (hourlyWeather != null) {
        hourlyWeatherString = " " + hourlyWeather + " "
    }

    view.setText(hourlyWeatherString)
}

@BindingAdapter("hours", "position", "time_zone_string", requireAll = true)
fun showHours(view: TextView, hourlyInfo: List<WeatherInfo.Hourly>?, position: Int, timeZoneString: String) {

    // ミリ秒に設定するため、秒に1000をかけている
    val timeInMillis = hourlyInfo?.get(position)?.dt?.times(1000)
    var hourString = ""
    val timeZone = TimeZone.getTimeZone(timeZoneString)
    val calendar = Calendar.getInstance(timeZone)

    if (timeInMillis != null) {
        calendar.timeInMillis = timeInMillis
        hourString = " " + calendar.get(Calendar.HOUR_OF_DAY) + ":00" + " "
    }

    view.setText(hourString)
}

@BindingAdapter("hourly_rain", "position", requireAll = true)
fun showHourlyRain(view: TextView, hourlyInfo: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyRain = hourlyInfo?.get(position)?.rain?.`1h`
    var hourlyRainString = ""
    if (hourlyRain != null) {
        val roundedRain = (Math.round(hourlyRain  * 10.0) / 10.0)
        hourlyRainString = " " + roundedRain + "mm" +  " "
    }

    view.setText(hourlyRainString)
}

@BindingAdapter("should_be_rounded_value", "is_searched_weather_state")
fun showRoundedValue(view: TextView, value: Double?, isSearchedWeatherState: Boolean) {
    var roundedValueString = ""

    if (value != null && isSearchedWeatherState) {
        val roundedValue = Math.round(value)

        roundedValueString = " " + roundedValue + " "
        view.setText(roundedValueString)
    }
}


@BindingAdapter("current_rain", "is_searched_weather_state", requireAll = true)
fun showCurrentRain(view: TextView, currentRain: Double?, isSearchedWeatherState: Boolean) {
    var currentRainString = ""

    if (currentRain != null && isSearchedWeatherState) {
        if (currentRain > 0.1) {
            val roundedRain = (Math.round(currentRain * 10.0) / 10.0)
            currentRainString = " " + roundedRain + "mm" + " "
        } else {
            currentRainString = " " + "微量" + " "
        }
    }

    view.setText(currentRainString)
}

private fun getWeatherIcon(weatherIconID: String, context: Context): Drawable? {
    return when (weatherIconID) {
        "01d" -> {
            ContextCompat.getDrawable(context, R.drawable.clear_sky)
        }
        "02d" -> {
            ContextCompat.getDrawable(context, R.drawable.few_clouds)
        }
        "03d" -> {
            ContextCompat.getDrawable(context, R.drawable.scattered_clouds)
        }
        "04d" -> {
            ContextCompat.getDrawable(context, R.drawable.broken_clouds)
        }
        "09d" -> {
            ContextCompat.getDrawable(context, R.drawable.shower_rain)
        }
        "10d" -> {
            ContextCompat.getDrawable(context, R.drawable.rain)
        }
        "11d" -> {
            ContextCompat.getDrawable(context, R.drawable.thunderstorm)
        }
        "13d" -> {
            ContextCompat.getDrawable(context, R.drawable.snow)
        }
        "50d" -> {
            ContextCompat.getDrawable(context, R.drawable.mist)
        }
        else -> null
    }
}
