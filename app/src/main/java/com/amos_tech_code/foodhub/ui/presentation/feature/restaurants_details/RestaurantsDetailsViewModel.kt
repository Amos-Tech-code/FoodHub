package com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.model.response.FoodItemResponse
import com.amos_tech_code.foodhub.data.model.response.Restaurant
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
class RestaurantsDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<RestaurantEvent>(RestaurantEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent?>()
    val navigationEvents = _navigationEvent.asSharedFlow()

    var errorMsg = ""
    var errorDescription = ""

    private var foodItems : List<FoodItem>? = null

    fun getFoodItem(id: String) {

        if (foodItems != null) {
            _uiState.value = RestaurantEvent.Success(foodItems!!)
            return
        }
        viewModelScope.launch {
            _uiState.value = RestaurantEvent.Loading
            try {
                val response = safeApiCall {
                    foodApi.getRestaurantFoodItems(id)
                }

                when (response) {
                    is ApiResponse.Success -> {
                        foodItems = response.data.foodItems
                        _uiState.value = RestaurantEvent.Success(foodItems!!)
                    }
                    else -> {
                        val error =
                            (response as? ApiResponse.Error)?.code
                        when (error) {
                            401 -> {
                                errorMsg = "Unauthorized"
                                errorDescription = "You are not authorized to perform this action"
                            }
                            404 -> {
                                errorMsg = "Not Found"
                                errorDescription = "The requested resource was not found"
                            }
                            500 -> {
                                errorMsg = "Internal Server Error"
                                errorDescription = "An error occurred on the server"
                            }
                            503 -> {
                                errorMsg = "Service Unavailable"
                                errorDescription = "The service is currently unavailable"
                            }
                            else -> {
                                errorMsg = "Error"
                                errorDescription = "An unknown error occurred"
                            }
                        }
                        _uiState.value = RestaurantEvent.Error
                        _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RestaurantEvent.Error
                _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
            }

        }
    }


    sealed class RestaurantNavigationEvent {
        data object GoBack : RestaurantNavigationEvent()

        data object ShowErrorDialog : RestaurantNavigationEvent()

        data class NavigateToRestaurantDetails(val productId: String) : RestaurantNavigationEvent()
    }

    sealed class RestaurantEvent {
        data object Loading : RestaurantEvent()

        data object Nothing : RestaurantEvent()

        data object Error : RestaurantEvent()

        data class Success(val foodItems: List<FoodItem>) : RestaurantEvent()
    }
}