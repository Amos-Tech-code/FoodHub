package com.amos_tech_code.foodhub.ui.feature.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
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
    private val foodApi: FoodApi
) : ViewModel() {


    val listOfStatus = OrderUtils.OrderStatus.entries.map { it.name }

    private val _uiState = MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<OrderDetailsEvent?>()
    val event = _event.asSharedFlow()
    var order: Order? = null

    fun getOrderDetails(orderID: String) {
        viewModelScope.launch {
            _uiState.value = OrderDetailsUiState.Loading
            val result = safeApiCall { foodApi.getOrderDetails(orderID) }
            when (result) {
                is ApiResponse.Success -> {
                    _uiState.value = OrderDetailsUiState.Success(result.data)
                    order = result.data
                }

                is ApiResponse.Error -> {
                    _uiState.value = OrderDetailsUiState.Error
                }

                else -> {
                    _uiState.value = OrderDetailsUiState.Error
                }
            }
        }
    }

    fun updateOrderStatus(orderID: String, status: String) {
        viewModelScope.launch {
            val result =
                safeApiCall { foodApi.updateOrderStatus(orderID, mapOf("status" to status)) }
            when (result) {
                is ApiResponse.Success -> {
                    _event.emit(OrderDetailsEvent.ShowPopUp("Order Status updated"))
                    getOrderDetails(orderID)
                }

                else -> {
                    _event.emit(OrderDetailsEvent.ShowPopUp("Order Status update failed"))
                }
            }
        }
    }

    sealed class OrderDetailsUiState {
        data object Loading : OrderDetailsUiState()

        data class Success(val order: Order) : OrderDetailsUiState()

        data object Error : OrderDetailsUiState()
    }

    sealed class OrderDetailsEvent {
        data object NavigateBack : OrderDetailsEvent()

        data class ShowPopUp(val msg: String) : OrderDetailsEvent()
    }

}