package com.amos_tech_code.foodhub.ui.presentation.navigation

import com.amos_tech_code.foodhub.data.model.response.FoodItem
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object AuthScreen

@Serializable
object Home

@Serializable
data class RestaurantsDetails(
    val name: String,
    val imgUrl: String,
    val restaurantId: String,
    //val description: String
)

@Serializable
data class FoodDetails(val foodItem: FoodItem)