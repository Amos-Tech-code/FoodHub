package com.amos_tech_code.foodhub.ui.feature.restaurants_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.FoodItem
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
                    is ApiResponse.Error -> {
                        _uiState.value = RestaurantEvent.Error(response.message)
                    }
                    is ApiResponse.Exception -> {
                        _uiState.value = RestaurantEvent.Error(response.exception.message?: "Something went wrong")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RestaurantEvent.Error(e.message ?: "Something went wrong")
            }

        }
    }


    sealed class RestaurantNavigationEvent {

        data object GoBack : RestaurantNavigationEvent()

        data object ShowErrorDialog : RestaurantNavigationEvent()

    }

    sealed class RestaurantEvent {
        data object Loading : RestaurantEvent()

        data object Nothing : RestaurantEvent()

        data class Error(val message: String) : RestaurantEvent()

        data class Success(val foodItems: List<FoodItem>) : RestaurantEvent()
    }
}