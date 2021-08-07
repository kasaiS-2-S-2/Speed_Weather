package com.kasai.speed_weather_new.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kasai.speed_weather_new.R
import com.kasai.speed_weather_new.databinding.HourlyWeatherInfoListItemBinding
import com.kasai.speed_weather_new.viewModel.WeatherInfoViewModel

class HourlyWeatherInfoListAdapter(private val viewModel: WeatherInfoViewModel, private val parentLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<HourlyWeatherInfoListAdapter.HourlyWeatherInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherInfoViewHolder {
        val binding = DataBindingUtil.inflate<HourlyWeatherInfoListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.hourly_weather_info_list_item,
            parent,
            false
        )
        return HourlyWeatherInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherInfoViewHolder, position: Int) {
        holder.binding.weatherInfoViewModel = viewModel
        holder.binding.position = position
        //ここでviewholderのlifecycleOwnerにセットする！
        holder.binding.lifecycleOwner = parentLifecycleOwner
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        val dataSize = viewModel.weatherInfoLiveData.value?.hourly?.size
        if (dataSize != null) {
            return dataSize
        } else {
            return 0;
        }
    }

    class HourlyWeatherInfoViewHolder(val binding: HourlyWeatherInfoListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
