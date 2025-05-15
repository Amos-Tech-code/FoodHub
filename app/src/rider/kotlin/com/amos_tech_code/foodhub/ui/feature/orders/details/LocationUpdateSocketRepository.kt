package com.amos_tech_code.foodhub.ui.feature.orders.details

import com.amos_tech_code.foodhub.data.SocketService
import com.amos_tech_code.foodhub.data.model.response.SocketLocationModel
import com.amos_tech_code.foodhub.location.LocationManager
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.LocationUpdateBaseRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LocationUpdateSocketRepository @Inject constructor(
    socketService: SocketService,
    private val locationManager: LocationManager
) : LocationUpdateBaseRepository(socketService) {

    private val _socketConnection = MutableStateFlow<SocketConnection>(SocketConnection.Disconnected)
    val socketConnection = _socketConnection.asStateFlow()

    override val messages = socketService.messages

    override fun connectSocket(
        orderId: String,
        riderId: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentLocation = getUserLocation()
                socketService.connectSocket(orderId, riderId, currentLocation.latitude, currentLocation.longitude)
                _socketConnection.value = SocketConnection.Connected
                locationManager.startLocationUpdate()

                while (socketConnection.value == SocketConnection.Connected) {
                    locationManager.locationUpdate.collectLatest {
                        it?.let {
                            val item = SocketLocationModel(
                                orderId = orderId,
                                riderId = riderId,
                                latitude = it.latitude,
                                longitude = it.longitude
                            )
                            socketService.sendMessage(Json.encodeToString(item))
                        }
                    }
                }
            } catch (e: Exception) {
                _socketConnection.value = SocketConnection.Disconnected
                locationManager.stopLocationUpdate()
                e.printStackTrace()
            }
        }
    }


    override fun disconnectSocket() {
        try {
            locationManager.stopLocationUpdate()
            socketService.disconnectSocket()
            _socketConnection.value = SocketConnection.Disconnected
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendMessage(message: String) {
        socketService.sendMessage(message)
    }

    suspend fun getUserLocation(): LatLng {
        return LatLng(0.0, 0.0)
    }


}

sealed class SocketConnection {
    data object Connected : SocketConnection()

    data object Disconnected : SocketConnection()
}