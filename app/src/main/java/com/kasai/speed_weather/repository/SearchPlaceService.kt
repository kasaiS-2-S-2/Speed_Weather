package com.kasai.speed_weather.repository

import com.kasai.speed_weather.model.PlaceInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchPlaceService {
    @GET("maps/api/place/findplacefromtext/json?")
    suspend fun getPlaceInfo(
        @Query("input") input: String,
        @Query("inputtype") inputType: String,
        @Query("fields") fields: String,
        @Query("key") key: String
    ): Response<PlaceInfo>
}