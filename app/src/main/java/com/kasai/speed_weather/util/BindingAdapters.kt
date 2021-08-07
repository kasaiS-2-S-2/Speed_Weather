/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kasai.speed_weather.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kasai.speed_weather.model.WeatherInfo

@BindingAdapter("app:showAllHourlyTemp")
fun showAllHourlyTemp(view: TextView, hourlyWeatherTemps: List<WeatherInfo.Hourly>?) {

    var hourlyTemps = ""
    if (hourlyWeatherTemps != null) { //ここらへん汚い書き方な気がする
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
