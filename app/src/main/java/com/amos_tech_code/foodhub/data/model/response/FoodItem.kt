package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String? = null,
    val createdAt: String? = null,
    val description: String,
    val id: String? = null,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val restaurantId: String
)