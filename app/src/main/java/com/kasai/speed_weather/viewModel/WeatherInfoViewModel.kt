package com.kasai.speed_weather.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasai.speed_weather.model.WeatherInfo
import com.kasai.speed_weather.repository.WeatherInfoRepository
import kotlinx.coroutines.launch

class WeatherInfoViewModel : ViewModel() {
    private val repository = WeatherInfoRepository.instance

    //監視対象のLiveData
    var weatherInfoLiveData: MutableLiveData<WeatherInfo> = MutableLiveData()

    //ViewModel初期化時にロード
    init {
        loadWeatherInfo()
    }

    private fun loadWeatherInfo() {
        //viewModelScope->ViewModel.onCleared() のタイミングでキャンセルされる CoroutineScope
        viewModelScope.launch {
            try {
                // 実行時は、appIDを自分のやつに書き換えする
                val request = repository.getWeatherInfo("35.68", "139.77", "minutely", "apikey")
                Log.d("AAAAAAAAAAAAAAAA1", "AAAAAAAAAAAAAAAAAAAAA")
                weatherInfoLiveData.postValue(request.body())
                if (request.isSuccessful) {
                    //データを取得したら、LiveDataを更新
                    weatherInfoLiveData.postValue(request.body())
                    Log.d("AAAAAAAAAAAAAAAAAAAAA2", "AAAAAAAAAAAAAAAAAAAAA")
                }
            } catch (e: Exception) {
                e.stackTrace

                Log.d("AAAAAAAAAAAAAAAA3", "AAAAAAAAAAAAAAAAAAAAA")
                e.printStackTrace()
            }
        }
    }
}