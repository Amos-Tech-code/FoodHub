package com.amos_tech_code.foodhub.ui.presentation.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.model.response.SocketLocation
import com.amos_tech_code.foodhub.data.model.response.SocketLocationResponse
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

abstract class OrderDetailsBaseViewModel(
    private val locationUpdateSocketRepository: LocationUpdateBaseRepository
)  : ViewModel() {

    init {
        processLocationUpdate()
    }

    private val _locationUpdate = MutableStateFlow<SocketLocation?>(null)
    val locationUpdate = _locationUpdate.asStateFlow()

    private fun processLocationUpdate() {
        viewModelScope.launch {
            locationUpdateSocketRepository.messages.collectLatest {
                if (it.isEmpty())
                    return@collectLatest
                val response = Json.decodeFromString(SocketLocationResponse.serializer(),it)
                _locationUpdate.value = SocketLocation(
                    response.currentLocation,
                    response.deliveryPhase,
                    response.estimatedTime,
                    response.finalDestination,
                    response.nextStop,
                    PolyUtil.decode(response.polyline)
                )
            }
        }

    }

    protected fun connectSocket(orderID: String, riderID: String) {
        locationUpdateSocketRepository.connectSocket(
            orderId = orderID,
            riderId = riderID,
        )
    }

    protected fun disconnectSocket() {
        locationUpdateSocketRepository.disconnectSocket()
    }
}