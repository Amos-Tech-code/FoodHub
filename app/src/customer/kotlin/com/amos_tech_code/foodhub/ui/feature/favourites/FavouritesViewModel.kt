package com.amos_tech_code.foodhub.ui.feature.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.UIFoodItem
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.model.response.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavouritesState>(FavouritesState.Loading)
    val uiState get() = _uiState

    private val _event = MutableSharedFlow<FavouritesEvent>()
    val event get() = _event.asSharedFlow()

    val favouriteFoodItems = mutableListOf<FoodItem>()
    val favouriteRestaurants = mutableListOf<Restaurant>()

    init {
        getFavourites()
    }

    fun getFavourites() {
        getFavouritesRestaurants()
        getFavouritesFoodItems()

    }

    private fun getFavouritesRestaurants() {
        val sampleRestaurants = listOf(
            Restaurant(
                address = "123 Main St",
                categoryId = "1",
                createdAt = "2022-01-01",
                distance = 1.5,
                id = "1",
                imageUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                latitude = 40.7128,
                longitude = -74.0060,
                name = "Restaurant 1",
                ownerId = "1"
            ),
            Restaurant(
                address = "4",
                categoryId = "1",
                createdAt = "2022-01-01",
                distance = 1.5,
                id = "2",
                imageUrl = "https://plus.unsplash.com/premium_photo-1686090448301-4c453ee74718?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                latitude = 40.7128,
                longitude = -74.0060,
                name = "Restaurant 2",
                ownerId = "1"
            )
        )

        viewModelScope.launch {
            delay(3000)
            favouriteRestaurants.addAll(sampleRestaurants)
            _uiState.value = FavouritesState.Favourites
        }
    }

    private fun getFavouritesFoodItems() {
        val sampleFoodItems = listOf(
            FoodItem(
                arModelUrl = "https://example.com/ar_models/burger_model.glb",
                createdAt = "2025-04-27T12:00:00Z",
                description = "A juicy beef burger topped with cheddar cheese, lettuce, and tomato.",
                id = "food_001",
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                name = "Cheeseburger Deluxe",
                price = 8.99,
                restaurantId = "resto_001"
            ),
            FoodItem(
                arModelUrl = "https://example.com/ar_models/pizza_model.glb",
                createdAt = "2025-04-26T15:30:00Z",
                description = "Thin-crust pizza with pepperoni, mozzarella, and homemade tomato sauce.",
                id = "food_002",
                imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=2076&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                name = "Pepperoni Pizza",
                price = 12.50,
                restaurantId = "resto_002"
            ),
            FoodItem(
                arModelUrl = "https://example.com/ar_models/sushi_model.glb",
                createdAt = "2025-04-25T18:45:00Z",
                description = "Fresh salmon and avocado wrapped in premium sushi rice and seaweed.",
                id = "food_003",
                imageUrl = "https://images.unsplash.com/photo-1553621042-f6e147245754?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                name = "Salmon Avocado Roll",
                price = 14.25,
                restaurantId = "resto_003"
            ),
        )
        viewModelScope.launch {
            delay(3000)
            favouriteFoodItems.addAll(sampleFoodItems)
            _uiState.value = FavouritesState.Favourites
        }
    }

    fun navigateToRestaurantDetails(restaurant: Restaurant) {
        viewModelScope.launch {
            _event.emit(
                FavouritesEvent.NavigateToRestaurantDetailsScreen(
                    restaurantName = restaurant.name,
                    restaurantImgUrl = restaurant.imageUrl,
                    restaurantId = restaurant.id
                )
            )
        }

    }

    fun navigateToFoodDetails(foodItem: FoodItem) {
        viewModelScope.launch {
            _event.emit(
                FavouritesEvent.NavigateToFoodDetailsScreen(
                    UIFoodItem.fromFoodItem(foodItem)
                )
            )
        }
    }

    sealed class FavouritesEvent {

        data class NavigateToRestaurantDetailsScreen(
            val restaurantName: String,
            val restaurantImgUrl: String,
            val restaurantId: String
        ) : FavouritesEvent()

        data class NavigateToFoodDetailsScreen(val foodItem: UIFoodItem) : FavouritesEvent()

    }


    sealed class FavouritesState {

        data object Loading : FavouritesState()

        data object Favourites : FavouritesState()

        data class Error(val message: String) : FavouritesState()

    }
}