package com.amos_tech_code.foodhub.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class LocationManager @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LocationManager"
        private const val LOCATION_TIMEOUT_MS = 10000L
        private const val FASTEST_INTERVAL_MS = 5000L
        private const val LOCATION_UPDATE_INTERVAL_MS = 10000L
    }

    private val _locationUpdate = MutableStateFlow<Location?>(null)
    val locationUpdate = _locationUpdate.asStateFlow()

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL_MS).apply {
        setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
    }.build()

    var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getLocation(): Flow<Location> = flow {
        // First try to get last known location quickly
        var location = fusedLocationProviderClient.lastLocation.await()

        // If no cached location, request fresh update with timeout
        if (location == null) {
            location = requestFreshLocation()
        }

        location?.let { emit(it) } ?: throw LocationUnavailableException("Unable to get location")
    }.flowOn(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshLocation(): Location? {
        return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        if (!continuation.isCompleted) {
                            continuation.resume(result.lastLocation) {
                                fusedLocationProviderClient.removeLocationUpdates(this)
                            }
                        }
                    }
                }

                val oneTimeRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
                    setDurationMillis(LOCATION_TIMEOUT_MS)
                    setMaxUpdates(1) // Only want one update
                }.build()

                try {
                    fusedLocationProviderClient.requestLocationUpdates(
                        oneTimeRequest,
                        callback,
                        Looper.getMainLooper()
                    ).addOnFailureListener { e ->
                        if (!continuation.isCompleted) {
                            continuation.resumeWithException(e)
                        }
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }

                continuation.invokeOnCancellation {
                    fusedLocationProviderClient.removeLocationUpdates(callback)
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocationUpdate() {
        if (locationCallback != null) {
            Log.w(TAG, "Location updates already started")
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                _locationUpdate.value = locationResult.lastLocation
                Log.d(TAG, "Location update: ${locationResult.lastLocation}")
            }
        }

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Log.d(TAG, "Location updates started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start location updates", e)
            locationCallback = null
        }
    }

    fun stopLocationUpdate() {
        locationCallback?.let { callback ->
            try {
                fusedLocationProviderClient.removeLocationUpdates(callback)
                Log.d(TAG, "Location updates stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop location updates", e)
            } finally {
                locationCallback = null
            }
        }
    }
}

class LocationUnavailableException(message: String = "Location unavailable") : Exception(message)