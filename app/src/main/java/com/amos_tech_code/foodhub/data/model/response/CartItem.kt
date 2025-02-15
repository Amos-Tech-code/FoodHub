package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val addedAt: String,
    val id: String,
    val menuItemId: FoodItem,
    var quantity: Int,
    val restaurantId: String,
    val userId: String
)