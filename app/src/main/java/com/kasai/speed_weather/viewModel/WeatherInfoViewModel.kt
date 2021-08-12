package com.kasai.speed_weather.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasai.speed_weather.model.WeatherInfo
import com.kasai.speed_weather.repository.WeatherInfoRepository
import kotlinx.coroutines.launch

class WeatherInfoViewModel : ViewModel() {
    private val repository = WeatherInfoRepository.instance
    //監視対象のLiveData
    private val _weatherInfoLiveData = MutableLiveData<WeatherInfo>()
    val weatherInfoLiveData: LiveData<WeatherInfo> = _weatherInfoLiveData

    //ViewModel初期化時にロード
    /*
    init {
        loadWeatherInfo()
    }
    */

    fun loadWeatherInfo(lat: Double, lon: Double) {
        Log.d("loadWeatherInfo", "lat" + " " + lat + " " + "lon" + " " + lon)
        //viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
        viewModelScope.launch {
            try {
                // 実行時は、appIDを自分のやつに書き換えする
                val request = repository.getWeatherInfo(lat.toString(), lon.toString(), "minutely", "")
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
}