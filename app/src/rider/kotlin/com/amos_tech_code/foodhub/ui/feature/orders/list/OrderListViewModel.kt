package com.amos_tech_code.foodhub.ui.feature.orders.list

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.DeliveryOrder
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
class OrderListViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _state = MutableStateFlow<OrdersListState>(OrdersListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrdersListEvent?>()
    val event get() = _event.asSharedFlow()

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            _state.value = OrdersListState.Loading
            val response = safeApiCall { foodApi.getActiveDeliveries() }
            when (response) {
                is ApiResponse.Success -> {
                    if(response.data.data.isEmpty()) {
                        _state.value = OrdersListState.Empty
                        return@launch
                    }
                    _state.value = OrdersListState.Success(response.data.data)
                }

                is ApiResponse.Error -> {
                    _state.value = OrdersListState.Error(response.message)
                }

                is ApiResponse.Exception -> {
                    _state.value = OrdersListState.Error(response.exception.message ?: "An unknown error occurred")
                }
            }
        }
    }

    sealed class OrdersListEvent {
        data object NavigateToOrderDetails : OrdersListEvent()
    }

    sealed class OrdersListState {
        data object Loading : OrdersListState()
        data object Empty : OrdersListState()
        data class Success(val orders: List<DeliveryOrder>) : OrdersListState()
        data class Error(val message: String) : OrdersListState()
    }

}