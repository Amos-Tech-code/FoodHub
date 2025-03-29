package com.amos_tech_code.foodhub.ui.presentation.navigation

import com.amos_tech_code.foodhub.data.model.response.FoodItem
import kotlinx.serialization.Serializable

interface NavRoute

@Serializable
object Login : NavRoute

@Serializable
object SignUp : NavRoute

@Serializable
object AuthScreen : NavRoute

@Serializable
object Home : NavRoute

@Serializable
data class RestaurantsDetails(
    val name: String,
    val imgUrl: String,
    val restaurantId: String,
    //val description: String
) : NavRoute

@Serializable
data class FoodDetails(val foodItem: FoodItem) : NavRoute

@Serializable
data object Cart : NavRoute

@Serializable
data object Notification : NavRoute

@Serializable
data object AddressList : NavRoute

@Serializable
data object AddAddress : NavRoute

@Serializable
data class OrderSuccess(val orderId: String) : NavRoute

@Serializable
data object OrderList : NavRoute

@Serializable
data class OrderDetails(val orderId: String) : NavRoute