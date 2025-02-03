package com.amos_tech_code.foodhub.ui.presentation.feature.food_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.request.AddToCartRequest
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoodDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi

) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsUiState>(FoodDetailsUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<FoodDetailsNavigationEvent?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _quantity = MutableStateFlow<Int>(0)
    val quantity = _quantity.asStateFlow()

    fun incrementQuantity() {
        _quantity.value++
    }

    fun decrementQuantity() {
        if (_quantity.value > 0) {
            _quantity.value--
        }
    }

    fun addToCart(
        restaurantId: String,
        foodItemId: String
    ) {
        viewModelScope.launch {
            _uiState.value = FoodDetailsUiState.Loading
            val response = safeApiCall {
                foodApi.addToCart(
                    request = AddToCartRequest(
                        restaurantId = restaurantId,
                        menuItemId = foodItemId,
                        quantity = _quantity.value
                    )
                )
            }

            when (response) {
                is ApiResponse.Error -> {
                    _uiState.value = FoodDetailsUiState.Error(message = response.message)
                    _navigationEvent.emit(FoodDetailsNavigationEvent.ShowErrorDialog(response.message))
                }
                is ApiResponse.Success -> {
                    _uiState.value = FoodDetailsUiState.Nothing
                    _navigationEvent.emit(FoodDetailsNavigationEvent.OnAddToCart)
                }
                else -> {
                    _uiState.value = FoodDetailsUiState.Error("Unknown error occurred")
                    _navigationEvent.emit(FoodDetailsNavigationEvent.ShowErrorDialog("Unknown error occurred"))
                }
            }
        }

    }

    sealed class FoodDetailsUiState {

        data object Loading : FoodDetailsUiState()

        data object Success : FoodDetailsUiState()

        data class Error(val message: String) : FoodDetailsUiState()

        data object Nothing : FoodDetailsUiState()
    }

    sealed class FoodDetailsNavigationEvent {

        data object GoToCart : FoodDetailsNavigationEvent()

        data object OnAddToCart : FoodDetailsNavigationEvent()

        data class ShowErrorDialog(val message: String) : FoodDetailsNavigationEvent()

    }

}