package com.kasai.speed_weather.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.kasai.speed_weather.R
import com.kasai.speed_weather.databinding.SolutionBinding
import com.kasai.speed_weather.util.HourlyWeatherInfoListAdapter
import com.kasai.speed_weather.viewModel.SearchWeatherViewModel


const val TAG_OF_SEARCH_WEATHER_FRAGMENT = "SearchWeatherFragment"

class SearchWeatherFragment : Fragment() {

    private lateinit var binding: SolutionBinding

    private val searchWeatherViewModel: SearchWeatherViewModel by viewModels()

    private val requestPermissionLauncher = getRequestPermissionLauncher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("SearchWeatherFragment", "onCreateView")
        //githubにはapikeyのcommit禁止！!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Places.initialize(requireActivity().getApplicationContext(), requireActivity().getApplicationContext().getString(R.string.api_key_google_maps))

        //binding = DataBindingUtil.inflate(inflater, R.layout.solution, container, false) //dataBinding
        binding = SolutionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchWeatherFragment", "onViewCreated")
        binding.searchWeatherFragment = this
        binding.searchWeatherViewModel = searchWeatherViewModel
        observeWeatherInfoViewModel(searchWeatherViewModel)
    }

    //observe開始
    //通知の内容に応じた処理を行う
    private fun observeWeatherInfoViewModel(viewModel: SearchWeatherViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.weatherInfo.observe(viewLifecycleOwner, Observer { weatherInfo ->
            if (weatherInfo != null) {
                binding.searchWeatherViewModel = viewModel

                val hourlyTempsRecyclerView = binding.hourlyTempsView
                val adapter = HourlyWeatherInfoListAdapter(viewModel, this)
                hourlyTempsRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(activity)
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                hourlyTempsRecyclerView.layoutManager = layoutManager
            }
            Log.d("observeWeatherInfoVM", "observing")
        })
    }

    fun searchWeather() {
        if (searchWeatherViewModel.searchCurrentPlace.value!!) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                // 現在位置の天気情報を取得
                searchWeatherViewModel.requestWeatherOfCurrentPlace()
            }
        } else {
            // 入力された場所の天気を取得
            searchWeatherViewModel.requestWeatherOfSpecificPlace()
        }
    }

    fun printSearchCurrentPlace() {
        Log.d("searchCurrentPlace", searchWeatherViewModel.searchCurrentPlace.value.toString())
        Log.d("placeName", searchWeatherViewModel.placeName.value.toString())
    }

    private fun getRequestPermissionLauncher(): ActivityResultLauncher<String> {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    val toast: Toast = Toast.makeText(activity, R.string.current_place_permisstion_denied_meg, Toast.LENGTH_LONG)
                    toast.show()

                    // move to detail settings of this app
                    val appDetailSettingsIntent = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.kasai.speed_weather_new")
                    )
                    appDetailSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        startActivity(appDetailSettingsIntent)
                    } catch (error: ActivityNotFoundException){
                        // if failed, move to whole settings
                        val settingsIntent = Intent(android.provider.Settings.ACTION_SETTINGS)
                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(settingsIntent)
                    }

                }
            }

        return requestPermissionLauncher
    }

}
