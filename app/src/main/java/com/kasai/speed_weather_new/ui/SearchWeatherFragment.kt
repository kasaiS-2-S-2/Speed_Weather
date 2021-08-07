package com.kasai.speed_weather_new.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
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
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.kasai.speed_weather_new.R
import com.kasai.speed_weather_new.databinding.SolutionBinding
import com.kasai.speed_weather_new.model.WeatherInfo
import com.kasai.speed_weather_new.util.HourlyWeatherInfoListAdapter
import com.kasai.speed_weather_new.viewModel.CurrentPlaceInfoViewModel
import com.kasai.speed_weather_new.viewModel.WeatherInfoViewModel


const val TAG_OF_SEARCH_WEATHER_FRAGMENT = "SearchWeatherFragment"

class SearchWeatherFragment : Fragment() {

    private lateinit var binding: SolutionBinding

    private val weatherInfoViewModel: WeatherInfoViewModel by viewModels()
    //private lateinit var weatherInfoBinding: SolutionBinding

    private val currentPlaceInfoViewModel: CurrentPlaceInfoViewModel by viewModels()

    private val requestPermissionLauncher = getRequestPermissionLauncher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Initialize the SDK
        //gitにはapikeyのcommit禁止！!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Places.initialize(requireActivity().getApplicationContext(), "apikey")

        binding = DataBindingUtil.inflate(inflater, R.layout.solution, container, false) //dataBinding
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchWeatherFragment = this
        observeWeatherInfoViewModel(weatherInfoViewModel)
        Log.d("AAAAAAAAAAAAAAAA4", "AAAAAAAAAAAAAAAAAAAAA")
        //var button = requireActivity().findViewById<Button>(R.id.getCurrentPlaceButton)
        //button.setOnClickListener { getCurrentPlace() }
        //getCurrentPlace()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    //observe開始
    //通知の内容に応じた処理を行う
    private fun observeWeatherInfoViewModel(viewModel: WeatherInfoViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.weatherInfoLiveData.observe(viewLifecycleOwner, Observer { weatherInfo ->
            Log.d("AAAAAAAAAAAAAAAA5", "AAAAAAAAAAAAAAAAAAAAA")
            if (weatherInfo != null) {
                Log.d("AAAAAAAAAAAAAAAA6", "AAAAAAAAAAAAAAAAAAAAA")
                binding.weatherInfoViewModel = viewModel

                val hourlyTempsRecyclerView = binding.hourlyTempsView
                val adapter = HourlyWeatherInfoListAdapter(weatherInfoViewModel, this)
                hourlyTempsRecyclerView.adapter = adapter
                val layoutManager = LinearLayoutManager(activity)
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                hourlyTempsRecyclerView.layoutManager = layoutManager

                Log.d("AAAAAAAAAAAAAAAAAAAA",
                    (viewModel.weatherInfoLiveData.value as WeatherInfo).current.temp.toString() + "" + (viewModel.weatherInfoLiveData.value as WeatherInfo).current.clouds.toString()
                )
            }
        })
    }

    fun getCurrentPlace() {
        Log.d("WWWWWWWWWWWWWWWWW1", "WWWWWWWWWWWWWWWWW")
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            //val requestPermissionLauncher = getRequestPermissionLauncher()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            Log.d("WWWWWWWWWWWWWWWWW2", "WWWWWWWWWWWWWWWWW")
        } else {
            currentPlaceInfoViewModel.getCurrentPlace()
        }
    }

    //MVVMではない
    private fun getCurrentPlacea() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
        val placeFields2: List<Place.Field> = listOf(Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields2)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placesClient = Places.createClient(requireContext())
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        Log.d(
                            ContentValues.TAG,
                            "Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood}"
                        )

                        Log.d(
                            ContentValues.TAG,
                            "Place '${placeLikelihood.place.latLng?.latitude}' has likelihood: ${placeLikelihood.likelihood}"
                        )
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.d(ContentValues.TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            val requestPermissionLauncher = getRequestPermissionLauncher()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //MVVMではない
    private fun getRequestPermissionLauncher(): ActivityResultLauncher<String> {
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
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

                    val appDetailSettingsIntent = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.kasai.speed_weather_new")
                    )
                    appDetailSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        startActivity(appDetailSettingsIntent)
                    } catch (error: ActivityNotFoundException){
                        val settingsIntent = Intent(android.provider.Settings.ACTION_SETTINGS)
                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(settingsIntent)
                    }

                }
            }

        return requestPermissionLauncher
    }

}
