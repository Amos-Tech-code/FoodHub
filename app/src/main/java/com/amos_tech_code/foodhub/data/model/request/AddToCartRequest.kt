package com.amos_tech_code.foodhub.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val restaurantId: String,
    val menuItemId: String,
    val quantity: Int
)
