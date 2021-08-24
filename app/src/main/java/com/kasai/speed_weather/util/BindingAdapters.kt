package com.kasai.speed_weather.util

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kasai.speed_weather.model.WeatherInfo
import java.util.*

@BindingAdapter("hourlyTemps", "position", requireAll = true)
fun showHourlyTemp(view: TextView, hourlyTemps: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyTemp = hourlyTemps?.get(position)?.temp
    var hourlyTempString = ""
    if (hourlyTemp != null) {
        hourlyTempString = " " + Math.round(hourlyTemp) + " "
    }

    view.setText(hourlyTempString)
}

@BindingAdapter("hourlyWeathers", "position", requireAll = true)
fun showHourlyWeather(view: TextView, hourlyWeathers: List<WeatherInfo.Hourly>?, position: Int) {

    val hourlyWeather = hourlyWeathers?.get(position)?.weather?.get(0)?.description
    var hourlyWeatherString = ""
    if (hourlyWeather != null) {
        hourlyWeatherString = " " + hourlyWeather + " "
    }

    view.setText(hourlyWeatherString)
}

@BindingAdapter("hours", "position", "time_zone_string", requireAll = true)
fun showHours(view: TextView, hours: List<WeatherInfo.Hourly>?, position: Int, timeZoneString: String) {

    // ミリ秒に設定するため、秒に1000をかけている
    val timeInMillis = hours?.get(position)?.dt?.times(1000)
    var hourString = ""
    val timeZone = TimeZone.getTimeZone(timeZoneString)
    val calendar = Calendar.getInstance(timeZone)

    if (timeInMillis != null) {
        calendar.timeInMillis = timeInMillis
        hourString = " " + calendar.get(Calendar.HOUR_OF_DAY) + ":00" + " "
    }

    view.setText(hourString)
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


@BindingAdapter("rain", "is_searched_weather_state")
fun showRain(view: TextView, rain: Double?, isSearchedWeatherState: Boolean) {
    var rainString = ""

    if (rain != null && isSearchedWeatherState) {
        val roundedRain = Math.round(rain)

        rainString = " " + roundedRain + "mm" + " "
        view.setText(rainString)
    }
}
