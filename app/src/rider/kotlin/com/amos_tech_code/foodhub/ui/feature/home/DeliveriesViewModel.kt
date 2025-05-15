package com.amos_tech_code.foodhub.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.response.Deliveries
import com.amos_tech_code.foodhub.data.model.response.DeliveriesListResponse
import com.amos_tech_code.foodhub.data.model.response.GenericMsgResponse
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveriesViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val foodHubSession: FoodHubSession
) : ViewModel() {

    private val _deliveriesState = MutableStateFlow<DeliveriesState>(DeliveriesState.Loading)
    val deliveriesState = _deliveriesState.asStateFlow()

    private val _deliveriesEvent = MutableSharedFlow<DeliveriesEvent>()
    val deliveriesEvent = _deliveriesEvent.asSharedFlow()

    val deliveries = MutableStateFlow<DeliveriesListResponse?>(null)

    init {
        getDeliveries()
    }

    fun getDeliveries() {
        viewModelScope.launch {
            try {
                _deliveriesState.value = DeliveriesState.Loading
                val response = safeApiCall { foodApi.getAvailableDeliveries() }
                when (response) {
                    is ApiResponse.Success -> {
                        _deliveriesState.value = DeliveriesState.Success(response.data.data)
                        deliveries.value = response.data
                    }

                    is ApiResponse.Error -> {
                        _deliveriesState.value = DeliveriesState.Error(response.message)
                    }

                    is ApiResponse.Exception -> {
                        _deliveriesState.value = DeliveriesState.Error(response.exception.message ?: "An unknown error occurred")
                    }
                }
            } catch (e: Exception) {
                _deliveriesState.value = DeliveriesState.Error(e.message ?: "An unknown error occurred")
            }
        }

    }


    fun deliveryAccepted(delivery: Deliveries) {
        viewModelScope.launch {
            _deliveriesState.value = DeliveriesState.Loading
            try {
                val response = safeApiCall { foodApi.acceptDelivery(delivery.orderId) }
                processDeliveryStateUpdate(response)
            } catch (e: Exception) {
                _deliveriesState.value = DeliveriesState.Success(deliveries.value?.data!!)
            }
        }
    }

    fun deliveryRejected(delivery: Deliveries) {
        viewModelScope.launch {
            viewModelScope.launch {
                _deliveriesState.value = DeliveriesState.Loading
                try {
                    val response = safeApiCall { foodApi.rejectDelivery(delivery.orderId) }
                    processDeliveryStateUpdate(response)
                } catch (e: Exception) {
                    _deliveriesState.value = DeliveriesState.Success(deliveries.value?.data!!)
                }
            }
        }
    }


    private suspend fun processDeliveryStateUpdate(response: ApiResponse<GenericMsgResponse>) {

        when (response) {
            is ApiResponse.Success -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                getDeliveries()
            }

            is ApiResponse.Error -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                _deliveriesEvent.emit(DeliveriesEvent.ShowError(response.message))
            }

            is ApiResponse.Exception -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                _deliveriesEvent.emit(DeliveriesEvent.ShowError(response.exception.message ?: "An unknown error occurred"))
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            foodHubSession.clearSession()
            _deliveriesEvent.emit(DeliveriesEvent.NavigateToLogin)
        }
    }


    sealed class DeliveriesState {
        data object Loading : DeliveriesState()

        data class Success(val deliveries: List<Deliveries>) : DeliveriesState()

        data class Error(val message: String) : DeliveriesState()
    }

    sealed class DeliveriesEvent {
        data object NavigateToOrderDetails : DeliveriesEvent()

        data object NavigateToLogin : DeliveriesEvent()

        data class ShowError(val message: String) : DeliveriesEvent()
    }

}