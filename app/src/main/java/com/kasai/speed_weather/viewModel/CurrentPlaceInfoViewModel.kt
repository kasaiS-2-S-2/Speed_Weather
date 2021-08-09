package com.kasai.speed_weather.viewModel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse


class CurrentPlaceInfoViewModel(application: Application) : AndroidViewModel(application) {

    // 緯度(初期値は東京駅に設定)
    private var _lat: Double = 35.68
    val lat = _lat

    //経度(初期値は東京駅に設定)
    private var _lon: Double = 139.77
    val lon = _lon


    fun updateCurrentPlaceInfo() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
        val placeFields2: List<Place.Field> = listOf(Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields2)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getApplication<Application>(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val placesClient = Places.createClient(getApplication<Application>())
            val placeResponse = placesClient.findCurrentPlace(request)


            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("placeResponse", "success")
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    val mostAccurateCurrentPlaceInfo = getMostAccurateCurrentLocation(task)
                    val latResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.latitude
                    val lonResult = mostAccurateCurrentPlaceInfo?.place?.latLng?.longitude

                    if (latResult != null && lonResult != null) {
                        _lat = latResult
                        _lon = lonResult
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
            //val requestPermissionLauncher = getRequestPermissionLauncher()
            //requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

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

