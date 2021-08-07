package com.kasai.speed_weather.repository

import com.kasai.speed_weather.model.WeatherInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {
    @GET("data/2.5/onecall?")
    suspend fun getWeatherInfo(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appID: String
    ): Response<WeatherInfo>
}
