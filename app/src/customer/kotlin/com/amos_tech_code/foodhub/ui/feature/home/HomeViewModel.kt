package com.amos_tech_code.foodhub.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.response.Category
import com.amos_tech_code.foodhub.data.model.response.FoodItem
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
class HomeViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val foodHubSession: FoodHubSession
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _popularFoodItemsState = MutableStateFlow<PopularFoodItemState>(PopularFoodItemState.Loading)
    val popularFoodItemsState = _popularFoodItemsState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeScreenNavigationEvents?>()
    val navigationEvents = _navigationEvent.asSharedFlow()

    private var categories = emptyList<Category>()
    private var restaurants = emptyList<Restaurant>()
    private var popularFoodItems = emptyList<FoodItem>()

    var imageUrl = foodHubSession.getProfilePicUrl()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadHomeScreenData()
    }

    fun retry() {
        _uiState.value = HomeScreenState.Loading
        loadHomeScreenData()
    }

    fun retryPopularFoodItems() {
        _popularFoodItemsState.value = PopularFoodItemState.Loading
        if (restaurants.isNotEmpty()) {
            getPopularFoodItems(restaurants[0].id)
        } else {
            loadHomeScreenData()
        }
    }

    private fun loadHomeScreenData() {
        viewModelScope.launch {
            _uiState.value = HomeScreenState.Loading

            val categoriesResult = getCategories()
            val restaurantsResult = getPopularRestaurants()

            val hasError = listOf(categoriesResult, restaurantsResult).any { it is ApiResponse.Error || it is ApiResponse.Exception }

            if (hasError) {
                val message = listOf(categoriesResult, restaurantsResult)
                    .mapNotNull {
                        when (it) {
                            is ApiResponse.Error -> it.formatMsg()
                            is ApiResponse.Exception -> it.exception.message
                            else -> null
                        }
                    }
                    .distinct()
                    .joinToString("\n")

                _uiState.value = HomeScreenState.Error(message.ifEmpty { "Something went wrong." })
                return@launch
            }

            categories = (categoriesResult as ApiResponse.Success).data
            restaurants = (restaurantsResult as ApiResponse.Success).data
            // Trigger fetch of popular food items from the first restaurant
            if (restaurants.isNotEmpty()) {
                getPopularFoodItems(restaurants[0].id)
            }
            _uiState.value = HomeScreenState.Success(
                categories = categories,
                restaurants = restaurants
            )
        }
    }


    private suspend fun getCategories(): ApiResponse<List<Category>> {
        if (categories.isNotEmpty()) {
            return ApiResponse.Success(categories)
        }
        return safeApiCall {
            foodApi.getCategories()
        }.let { response ->
            when (response) {
                is ApiResponse.Success -> ApiResponse.Success(response.data.data)
                is ApiResponse.Error -> response
                is ApiResponse.Exception -> response
            }
        }
    }

    private suspend fun getPopularRestaurants(): ApiResponse<List<Restaurant>> {
        if (restaurants.isNotEmpty()) {
            getPopularFoodItems(restaurants[0].id)
            return ApiResponse.Success(restaurants)
        }
        return safeApiCall {
            foodApi.getRestaurants(-0.7189139234125226, 37.14924439362768)
        }.let { response ->
            when (response) {
                is ApiResponse.Success -> {
                    ApiResponse.Success(response.data.data)
                }
                is ApiResponse.Error -> response
                is ApiResponse.Exception -> response
            }
        }
    }

    private fun getPopularFoodItems(id: String) {

        if (popularFoodItems.isNotEmpty()) {
            _popularFoodItemsState.value = PopularFoodItemState.Success(popularFoodItems)
            return
        }
        viewModelScope.launch {
            _popularFoodItemsState.value = PopularFoodItemState.Loading
            try {
                val response = safeApiCall {
                    foodApi.getRestaurantFoodItems(id)
                }

                when (response) {
                    is ApiResponse.Success -> {
                        popularFoodItems = response.data.foodItems
                        _popularFoodItemsState.value = PopularFoodItemState.Success(popularFoodItems)
                    }
                    is ApiResponse.Error -> {
                        _popularFoodItemsState.value = PopularFoodItemState.Error(response.message)
                    }
                    is ApiResponse.Exception -> {
                        _popularFoodItemsState.value = PopularFoodItemState.Error(response.exception.message?: "Something went wrong")
                    }
                }
            } catch (e: Exception) {
                _popularFoodItemsState.value = PopularFoodItemState.Error(e.message ?: "Something went wrong")
            }

        }
    }


    fun onRestaurantSelected(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeScreenNavigationEvents.NavigateToDetail(
                    it.name,
                    it.imageUrl,
                    it.id
                )
            )
        }

    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun logOut() {
        viewModelScope.launch {
            foodHubSession.clearSession()
            _navigationEvent.emit(HomeScreenNavigationEvents.NavigateToLogin)
        }
    }

    sealed class HomeScreenState {
        data object Loading: HomeScreenState()

        data class Error(val message: String) : HomeScreenState()

        data class Success(
            val categories: List<Category> = emptyList(),
            val restaurants: List<Restaurant> = emptyList()
        ): HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents {
        data class NavigateToDetail(
            val name: String,
            val imageUrl: String,
            val id: String
        ): HomeScreenNavigationEvents()

        data object NavigateToLogin : HomeScreenNavigationEvents()
    }

    sealed class PopularFoodItemState {
        data object Loading: PopularFoodItemState()
        data class Error(val message: String) : PopularFoodItemState()
        data class Success(val foodItems: List<FoodItem>) : PopularFoodItemState()
    }

}