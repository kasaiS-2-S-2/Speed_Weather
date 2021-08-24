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
import com.kasai.speed_weather.R
import com.kasai.speed_weather.model.PlaceInfo
import com.kasai.speed_weather.model.WeatherInfo
import com.kasai.speed_weather.repository.InfoRepository
import kotlinx.coroutines.launch
import java.util.*


class SearchWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InfoRepository.instance
    //監視対象のLiveData
    private val _weatherInfo = MutableLiveData<WeatherInfo>()
    val weatherInfo: LiveData<WeatherInfo> = _weatherInfo

    // 入力された場所の文字列
    val placeName = MutableLiveData("")
    // 現在地を検索するかのラジオボタンの値
    val searchCurrentPlace = MutableLiveData(false)
    private var placeInfo: PlaceInfo = PlaceInfo(emptyList(), "")
    // 調べる場所の経度(初期値は南極に設定)
    private var lat: Double = -75.250973
    // 調べる場所の緯度(初期値は南極に設定)
    private var lon: Double = -0.071389
    // 天気を調べる場所の住所
    private val _searchedPlaceAddress = MutableLiveData<String>()
    val searchedPlaceAddress = _searchedPlaceAddress
    private val _isSearchedWeatherState = MutableLiveData(false)
    val isSearchedWeatherState: LiveData<Boolean> = _isSearchedWeatherState


    fun printSearchCurrentPlace() {
        Log.d("searchCurrentPlace", searchCurrentPlace.value.toString())
        Log.d("placeName", placeName.value.toString())
    }

    fun requestWeatherOfCurrentPlace() {
        // Use fields to define the data types to return.
        val placeFieldsAddress: List<Place.Field> = listOf(Place.Field.ADDRESS)
        // Use the builder to create a FindCurrentPlaceRequest.
        val requestAddress: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFieldsAddress)

        // 現在地の住所を取得する
        loadCurrentPlaceAddress(requestAddress)
    }

    fun requestWeatherOfSpecificPlace() {
        viewModelScope.launch {
            // viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
            loadSpecificPlaceInfo(placeName.value.toString())
            loadWeatherInfo(lat, lon)
        }
    }

    // 現在地の住所を取得する関数
    private fun loadCurrentPlaceAddress(requestAddress: FindCurrentPlaceRequest) {
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getApplication<Application>(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placesClientAddress = Places.createClient(getApplication<Application>())
            val placeResponseAddress = placesClientAddress.findCurrentPlace(requestAddress)

            placeResponseAddress.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mostAccurateCurrentPlaceInfo = getMostAccurateCurrentLocation(task)
                    val currentPlaceAddress = mostAccurateCurrentPlaceInfo?.place?.address

                    if (currentPlaceAddress != null) {
                        _searchedPlaceAddress.value = currentPlaceAddress!!

                        val placeFieldsLatLng: List<Place.Field> = listOf(Place.Field.LAT_LNG)
                        val requestLatLng: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFieldsLatLng)
                        // 住所の取得が成功したら、次は現在地の緯度経度を取得する
                        loadCurrentPlaceLatLng(requestLatLng)
                    } else {
                        // 現在地の情報取得に失敗した場合は南極の天気を取得する
                        loadWeatherInfo(-75.250973, -0.071389)
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.d(ContentValues.TAG, "Place not found: ${exception.statusCode}")
                    }
                    // 現在地の情報取得に失敗した場合は南極の天気を取得する
                    loadWeatherInfo(-75.250973, -0.071389)
                }
            }
        }
    }

    // 現在地の緯度経度を取得する関数
    private fun loadCurrentPlaceLatLng(requestLatLng: FindCurrentPlaceRequest) {
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getApplication<Application>(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placesClientLatLng = Places.createClient(getApplication<Application>())
            val placeResponseLatLng = placesClientLatLng.findCurrentPlace(requestLatLng)

            placeResponseLatLng.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mostAccurateCurrentPlaceInfo = getMostAccurateCurrentLocation(task)
                    val latResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.latitude
                    val lonResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.longitude

                    if (latResult != null && lonResult != null) {
                        lat = latResult
                        lon = lonResult
                        // 現在地の情報取得に成功した場合は現在地の天気を取得する
                        loadWeatherInfo(lat, lon)
                    } else {
                        // 現在地の情報取得に失敗した場合は南極の天気を取得する
                        loadWeatherInfo(-75.250973, -0.071389)
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.d(ContentValues.TAG, "Place not found: ${exception.statusCode}")
                    }
                    // 現在地の情報取得に失敗した場合は南極の天気を取得する
                    loadWeatherInfo(-75.250973, -0.071389)
                }
            }

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

    private fun loadWeatherInfo(lat: Double, lon: Double) {
        Log.d("loadWeatherInfo", "lat" + " " + lat + " " + "lon" + " " + lon)
        //viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
        viewModelScope.launch {
            try {
                val appContext = getApplication<Application>().applicationContext
                // 実行時は、appkeyを自分のやつに書き換えする
                val request = repository.getWeatherInfo(lat.toString(), lon.toString(),
                    appContext.getString(R.string.parameter_open_weather_map_units),
                    appContext.getString(R.string.parameter_open_weather_map_exclude),
                    appContext.getString(R.string.api_key_open_weather_map))
                if (request.isSuccessful) {
                    //データを取得したら、LiveDataを更新
                    _weatherInfo.postValue(request.body())
                    _isSearchedWeatherState.postValue(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadSpecificPlaceInfo(placeName: String) {
        //viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
        //viewModelScope.launch {
            try {
                val appContext = getApplication<Application>().applicationContext
                // 実行時は、apikeyを自分のやつに書き換えする
                val request = repository.getPlaceInfo(placeName,
                    appContext.getString(R.string.parameters_google_maps_inputType),
                    appContext.getString(R.string.parameters_google_maps_fields),
                    appContext.getString(R.string.api_key_google_maps))

                if (request.isSuccessful) {
                    if (request.body() != null) {
                        placeInfo = request.body()!!
                        lat = placeInfo.candidates[0].geometry.location.lat
                        lon = placeInfo.candidates[0].geometry.location.lng
                        _searchedPlaceAddress.value = placeInfo.candidates[0].formatted_address
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        //}
    }
}

