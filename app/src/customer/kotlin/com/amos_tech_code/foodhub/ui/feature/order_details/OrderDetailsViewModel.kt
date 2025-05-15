package com.amos_tech_code.foodhub.ui.feature.order_details

import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.LocationUpdateBaseRepository
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.OrderDetailsBaseViewModel
import com.amos_tech_code.foodhub.utils.OrderUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi,
    locationUpdateRepository: LocationUpdateBaseRepository
) : OrderDetailsBaseViewModel(locationUpdateRepository) {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderDetailsEvent>()
    val event get() = _event.asSharedFlow()


    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            val result = safeApiCall { foodApi.getOrderDetails(orderId) }

            when (result) {
                is ApiResponse.Success -> {
                    _state.value = OrderDetailsState.OrderDetails(result.data)

                    if (result.data.status == OrderUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                        result.data.riderId?.let {
                            connectSocket(orderId, it)
                        }
                    } else {
                        if (result.data.status == OrderUtils.OrderStatus.DELIVERED.name
                            || result.data.status == OrderUtils.OrderStatus.CANCELLED.name
                            || result.data.status == OrderUtils.OrderStatus.REJECTED.name) {
                            disconnectSocket()
                        }
                    }

                }

                is ApiResponse.Error -> {
                    _state.value = OrderDetailsState.Error(result.message)
                }

                is ApiResponse.Exception -> {
                    _state.value =
                        OrderDetailsState.Error(result.exception.message ?: "An error occurred")
                }
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _event.emit(OrderDetailsEvent.NavigateBack)
        }
    }

    fun getImage(order: Order): Int {
        return when (order.status) {
            "Delivered" -> R.drawable.ic_delivered
            "Preparing" -> R.drawable.ic_preparing
            "On the way" -> R.drawable.ic_delivery
            else -> R.drawable.ic_pending
        }
    }

    sealed class OrderDetailsEvent {
        data object NavigateBack : OrderDetailsEvent()
    }

    sealed class OrderDetailsState {

        data object Loading : OrderDetailsState()

        data class OrderDetails(val order: Order) : OrderDetailsState()

        data class Error(val message: String) : OrderDetailsState()
    }
}