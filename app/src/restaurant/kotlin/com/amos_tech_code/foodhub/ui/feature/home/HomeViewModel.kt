package com.amos_tech_code.foodhub.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.response.Restaurant
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val session: FoodHubSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getRestaurantProfile()
    }
    private fun getRestaurantProfile() {
        viewModelScope.launch {

            try {
                val result = safeApiCall { foodApi.getRestaurantProfile() }

                when (result) {
                    is ApiResponse.Error -> {
                        _uiState.value = HomeScreenState.Failed(result.message)
                    }
                    is ApiResponse.Exception -> {
                        _uiState.value = HomeScreenState.Failed(result.exception.message ?: "An unknown error occurred")
                    }
                    is ApiResponse.Success -> {
                        _uiState.value = HomeScreenState.Success(result.data)
                        session.storeRestaurantId(result.data.id)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Failed(e.message ?: "Something went wrong")
            }
        }
    }

    fun retry() {
        getRestaurantProfile()
    }

    sealed class HomeScreenState {

        data object Loading : HomeScreenState()

        data class Failed(val message: String) : HomeScreenState()

        data class Success(val data: Restaurant) : HomeScreenState()
    }
}