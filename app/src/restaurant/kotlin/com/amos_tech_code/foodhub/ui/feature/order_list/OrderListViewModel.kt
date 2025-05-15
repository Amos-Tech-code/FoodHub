package com.amos_tech_code.foodhub.ui.feature.order_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.amos_tech_code.foodhub.utils.OrderUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    fun getOrderTypes(): List<String> {
        val types = OrderUtils.OrderStatus.entries.map { it.name }
        return types
    }

    private val _uiState = MutableStateFlow<OrdersScreenState>(OrdersScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getOrdersByType(status: String) {
        viewModelScope.launch {
            _uiState.value = OrdersScreenState.Loading
            val response = safeApiCall { foodApi.getRestaurantOrders(status) }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = OrdersScreenState.Success(response.data.orders)
                }

                else -> {
                    _uiState.value = OrdersScreenState.Failed
                }
            }
        }
    }

    sealed class OrdersScreenState {
        data object Loading : OrdersScreenState()

        data object Failed : OrdersScreenState()

        data class Success(val data: List<Order>) : OrdersScreenState()
    }

}