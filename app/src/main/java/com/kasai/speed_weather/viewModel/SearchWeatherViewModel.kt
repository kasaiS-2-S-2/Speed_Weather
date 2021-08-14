package com.kasai.speed_weather.viewModel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.kasai.speed_weather.model.WeatherInfo
import com.kasai.speed_weather.repository.WeatherInfoRepository
import kotlinx.coroutines.launch


class SearchWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherInfoRepository.instance
    //監視対象のLiveData
    private val _weatherInfoLiveData = MutableLiveData<WeatherInfo>()
    val weatherInfoLiveData: LiveData<WeatherInfo> = _weatherInfoLiveData

    // 経度(初期値はシカゴに設定)
    private var lat: Double = 41.8781

    // 緯度(初期値はシカゴに設定)
    private var lon: Double = -87.6297


    fun loadWeatherInfo(lat: Double, lon: Double) {
        Log.d("loadWeatherInfo", "lat" + " " + lat + " " + "lon" + " " + lon)
        //viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
        viewModelScope.launch {
            try {
                // 実行時は、appIDを自分のやつに書き換えする
                val request = repository.getWeatherInfo(lat.toString(), lon.toString(), "minutely", "ef1e1506d6f0c471d708abbc35d8ed7d")
                _weatherInfoLiveData.postValue(request.body())
                if (request.isSuccessful) {
                    //データを取得したら、LiveDataを更新
                    _weatherInfoLiveData.postValue(request.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestWeatherOfCurrentPlace() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getApplication<Application>(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placesClient = Places.createClient(getApplication<Application>())
            val placeResponse = placesClient.findCurrentPlace(request)

            placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("placeResponse", "success")

                        val mostAccurateCurrentPlaceInfo = getMostAccurateCurrentLocation(task)
                        val latResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.latitude
                        val lonResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.longitude

                        if (latResult != null && lonResult != null) {
                            lat = latResult
                            lon = lonResult

                            // 現在地の情報取得に成功した場合は現在地の天気を取得する
                            loadWeatherInfo(lat, lon)
                        } else {
                            // 現在地の情報取得に失敗した場合はシカゴの天気を取得する
                            loadWeatherInfo(41.8781, -87.6297)
                        }
                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            Log.d(ContentValues.TAG, "Place not found: ${exception.statusCode}")
                        }
                        // 現在地の情報取得に失敗した場合はシカゴの天気を取得する
                        loadWeatherInfo(41.8781, -87.6297)
                    }
            }

        } else {
            Log.d("CurrentPlaceInfoVM", "not granted")
        }
    }

    private fun getMostAccurateCurrentLocation(task: Task<FindCurrentPlaceResponse>): PlaceLikelihood? {
        val response = task.result
        var resultPlaceInfo: PlaceLikelihood? = response?.placeLikelihoods?.get(0)

        for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
            var maxLikelihood: Double = -1.0

            if (maxLikelihood == -1.0) {
                maxLikelihood = placeLikelihood.likelihood
                resultPlaceInfo = placeLikelihood
            } else {
                if (maxLikelihood < placeLikelihood.likelihood) {
                    maxLikelihood = placeLikelihood.likelihood
                    resultPlaceInfo = placeLikelihood
                }
            }
        }
        if (resultPlaceInfo != null) {
            return resultPlaceInfo
        } else {
            return null
        }
    }
}

