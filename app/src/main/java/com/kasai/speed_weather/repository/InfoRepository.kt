package com.kasai.speed_weather.repository

import com.kasai.speed_weather.model.PlaceInfo
import com.kasai.speed_weather.model.WeatherInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HTTPS_API_OPEN_WEATHER_MAP_URL = "https://api.openweathermap.org/"
const val HTTPS_API_MAPS_GOOGLEAPIS_URL = "https://maps.googleapis.com/"

class InfoRepository {

    companion object Factory {
        val instance: InfoRepository
            @Synchronized get() { //このアノテーションの意味確認！ → https://qiita.com/leebon93/items/c7f2ac357f36930ff77f
                return InfoRepository()
            }
    }

    private val retrofitOfWeather = Retrofit.Builder()
        .baseUrl(HTTPS_API_OPEN_WEATHER_MAP_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val searchWeatherService: SearchWeatherService = retrofitOfWeather.create(SearchWeatherService::class.java)
    suspend fun getWeatherInfo(lat: String, lon: String, exclude: String, appID: String): Response<WeatherInfo> =
        searchWeatherService.getWeatherInfo(lat, lon, exclude, appID)


    private val retrofitOfPlace = Retrofit.Builder()
        .baseUrl(HTTPS_API_MAPS_GOOGLEAPIS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val searchPlaceService: SearchPlaceService = retrofitOfPlace.create(SearchPlaceService::class.java)
    suspend fun getPlaceInfo(input: String, inputType: String, fields: String, key: String): Response<PlaceInfo> =
        searchPlaceService.getPlaceInfo(input, inputType, fields, key)

}