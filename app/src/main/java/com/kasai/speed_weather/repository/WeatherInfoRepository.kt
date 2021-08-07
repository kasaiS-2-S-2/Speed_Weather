package com.kasai.speed_weather.repository

import com.kasai.speed_weather.model.WeatherInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HTTPS_API_OPEN_WEATHER_MAP_URL = "https://api.openweathermap.org/"

class WeatherInfoRepository {

    companion object Factory {
        val instance: WeatherInfoRepository
            @Synchronized get() { //このアノテーションの意味確認！ → https://qiita.com/leebon93/items/c7f2ac357f36930ff77f
                return WeatherInfoRepository()
            }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(HTTPS_API_OPEN_WEATHER_MAP_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService: WeatherService = retrofit.create(WeatherService::class.java)

    suspend fun getWeatherInfo(lat: String, lon: String, exclude: String, appID: String): Response<WeatherInfo> =
        weatherService.getWeatherInfo(lat, lon, exclude, appID)
}