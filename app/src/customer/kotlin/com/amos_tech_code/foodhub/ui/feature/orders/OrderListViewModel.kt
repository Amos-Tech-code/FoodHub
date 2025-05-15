package com.amos_tech_code.foodhub.ui.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.data.model.response.OrderListResponse
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

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderListEvent>()
    val event get() = _event.asSharedFlow()

    private var orderResponse : OrderListResponse? = null

    init {
        getOrders()
    }

    fun navigateToDetails(order: Order) {
        viewModelScope.launch {
            _event.emit(OrderListEvent.NavigateToOrderDetailScreen(order))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _event.emit(OrderListEvent.NavigateBack)
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            _state.value = OrderListState.Loading
            val result = safeApiCall { foodApi.getOrders() }
            when (result) {
                is ApiResponse.Success -> {
                    orderResponse = result.data
                    _state.value = OrderListState.OrderList(result.data)
                }

                is ApiResponse.Error -> {
                    _state.value = OrderListState.Error(result.message)
                }

                is ApiResponse.Exception -> {
                    _state.value =
                        OrderListState.Error(result.exception.message ?: "An error occurred")
                }

            }
        }
    }

    sealed class OrderListEvent {
        data class NavigateToOrderDetailScreen(val order: Order) : OrderListEvent()

        data object NavigateBack : OrderListEvent()
    }

    sealed class OrderListState {
        data object Loading : OrderListState()

        data class OrderList(val orderList: OrderListResponse) : OrderListState()

        data class Error(val message: String) : OrderListState()
    }

}